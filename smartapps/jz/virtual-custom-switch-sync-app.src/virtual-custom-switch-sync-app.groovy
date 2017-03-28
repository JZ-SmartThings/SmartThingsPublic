/**
 *  Virtual Custom Switch Sync App v1.0.20170227
 *  Copyright 2017 JZ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
definition(
    name: "Virtual Custom Switch Sync App",
    namespace: "JZ",
    author: "JZ",
    description: "Synchronize a simulated/virtual switch with the Custom Switch of the Generic HTTP Device Handler. This helps with automation of the second button.",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("Choose your Generic HTTP Device Handler:") {
		input ("httpswitch", "capability.switch", title: "HTTP Device?", multiple: false, required: true)
			if(httpswitch != null){
			  paragraph "Device Handler: $httpswitch.typeName\r\n\r\nDetected Number of Endpoints: ${getEndpoints()}\r\n\r\nRecommended Type: ${getType()}"
			  input "virtualSwitchType", "enum", title: "Virtual Switch Type", value: getType() , multiple: false, required: true, options: ["Switch","Contact Sensor"]
			  app.updateSetting("virtualSwitchType", getType())
			}
	}
	section("Choose your Simulated, currently unlinked switch:") {
		input ("virtualswitch", "capability.switch", title: "Virtual Switch?", multiple: false, required: true)
	}
}


private getType() {
   String hasCapability = ""

   if (httpswitch.hasCapability("Switch")) {
      hasCapability = "Switch"
   }
   if ((httpswitch.hasCapability("Contact Sensor")) || (httpswitch.hasCapability("Sensor"))) {
      hasCapability = "Contact Sensor"
   }
    return hasCapability
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(httpswitch, "customswitch", switchOffHandler)
	subscribe(virtualswitch, "switch", virtualSwitchHandler)
}

def switchOffHandler(evt) {
	//log.debug "$httpswitch.name was turned " + httpswitch*.currentValue("customswitch")
	log.debug "switchOffHandler called with event: deviceId ${evt.deviceId} name:${evt.name} source:${evt.source} value:${evt.value} isStateChange: ${evt.isStateChange()} isPhysical: ${evt.isPhysical()} isDigital: ${evt.isDigital()} data: ${evt.data} device: ${evt.device}"
   	sendEvent(settings["virtualswitch"], [name:"switch", value:"$evt.value"])
}
def virtualSwitchHandler(evt) {
	log.debug "virtualSwitchHandler called with event: deviceId ${evt.deviceId} name:${evt.name} source:${evt.source} value:${evt.value} isStateChange: ${evt.isStateChange()} isPhysical: ${evt.isPhysical()} isDigital: ${evt.isDigital()} data: ${evt.data} device: ${evt.device}"
	if (now()-httpswitch*.currentValue("customTriggeredEPOCH")[0] > 3000) {
		httpswitch.off()
	}
}
