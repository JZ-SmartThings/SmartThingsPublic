/**
 *  Virtual 2nd Contact Sensor v1.0.20170408
 *  Copyright 2017 JZ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
metadata {
	definition (name: "Virtual 2nd Contact Sensor", namespace: "JZ", author: "JZ") {
		capability "Contact Sensor"
		capability "Sensor"
        capability "Refresh"

		command "open"
		command "close"
	}
	simulator {
		status "open": "contact:open"
		status "closed": "contact:closed"
	}
	tiles(scale: 2) {
		standardTile("contact", "device.contact", width: 6, height: 2) {
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#53a7c0")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#FF6600")
		}
		valueTile("sensor2Triggered", "device.sensor2Triggered", width: 6, height: 2, decoration: "flat") {
			state("default", label: 'Sensor 2 State Changed:\r\n${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("refreshTriggered", "device.refreshTriggered", width: 4, height: 2, decoration: "flat") {
			state("default", label: 'Refreshed:\r\n${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("refresh", "device.refresh", width: 2, height: 2, decoration: "flat") {
			state "default", label:'REFRESH', action: "refresh", icon: "st.secondary.refresh-icon", backgroundColor:"#53a7c0", nextState: "refreshing"
			state "refreshing", label: 'REFRESHING', action: "refresh", icon: "st.secondary.refresh-icon", backgroundColor: "#FF6600", nextState: "default"
		}
		main "contact"
		details (["contact","sensor2Triggered","refreshTriggered","refresh"])
	}
}

def refresh() {
	log.debug "refresh()"
	sendEvent(name: "refresh", value: new Date().format("yyyy-MM-dd h:mm:ss a", location.timeZone))
}

def parseORIG(String description) {
	def pair = description.split(":")
	createEvent(name: pair[0].trim(), value: pair[1].trim())
}

def parse(description) {
	def eventMap
	if (description.type == null) eventMap = [name:"$description.name", value:"$description.value"]
	else eventMap = [name:"$description.name", value:"$description.value", type:"$description.type"]
	createEvent(eventMap)
}

def open() {
	log.trace "open()"
	sendEvent(name: "contact", value: "open")
}

def close() {
	log.trace "close()"
    sendEvent(name: "contact", value: "closed")
}