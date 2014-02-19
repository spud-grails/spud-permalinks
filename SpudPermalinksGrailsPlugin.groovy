/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import spud.security.SpudSecurityBridge
import grails.plugin.webxml.FilterManager


class SpudPermalinksGrailsPlugin {
    def version = "0.1.0"
    def grailsVersion = "2.3 > *"
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
    def title       = "Spud Permalinks Plugin"
    def author      = "David Estes"
    def authorEmail = "destes@bcap.com"
    def description = "Creates a filter for redirecting urls from one location to another and provides an admin interface for defining these permalinks."
    def documentation = "https://github.com/spud-grails/spud-permalinks"
    def license = "APACHE"
    def organization = [name: "Bertram Labs", url: "http://www.bertramlabs.com/"]
    def issueManagement = [system: "GITHUB", url: "https://github.com/spud-grails/spud-permalinks/issues"]
    def scm = [url: "https://github.com/spud-grails/spud-permalinks"]


    def getWebXmlFilterOrder() {
        [SpudPermalinkPluginFilter: FilterManager.GRAILS_WEB_REQUEST_POSITION - 100]
    }

    def doWithWebDescriptor = { xml ->

        def filters = xml.filter[0]
        filters + {
            'filter' {
                'filter-name'('SpudPermalinkPluginFilter')
                'filter-class'('spud.permalinks.PermalinkFilter')
            }
        }

        def mappings = xml.'filter-mapping'[0]
        mappings + {
            'filter-mapping' {
                'filter-name'('SpudPermalinkPluginFilter')
                'url-pattern'('/*')
                dispatcher('REQUEST')
            }
        }
    }
}
