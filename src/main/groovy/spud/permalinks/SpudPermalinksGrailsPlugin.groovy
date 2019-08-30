package spud.permalinks

import grails.plugins.*
import groovy.util.logging.Slf4j
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.core.Ordered
import org.springframework.util.ClassUtils

@Slf4j
class SpudPermalinksGrailsPlugin extends Plugin {

	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "3.1.12 > *"
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
		"grails-app/views/error.gsp"
	]

	// TODO Fill in these fields
	def title = "Spud Permalinks Plugin" // Headline display name of the plugin
	def author = "David Estes"
	def authorEmail = "destes@bcap.com"
	def description = '''\
Creates a filter for redirecting urls from one location to another and provides an admin interface for defining these permalinks.
'''
	def profiles = ['web']

	// URL to the plugin's documentation
	def documentation = "http://grails.org/plugin/spud-permalinks"

	// Extra (optional) plugin metadata

	// License: one of 'APACHE', 'GPL2', 'GPL3'
	def license = "APACHE"

	// Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]
	def organization = [name: "Bertram Labs", url: "http://www.bertramlabs.com/"]

	// Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

	// Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]
	def issueManagement = [system: "GITHUB", url: "https://github.com/spud-grails/spud-permalinks/issues"]

	// Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]
	def scm = [url: "https://github.com/spud-grails/spud-permalinks"]

	Closure doWithSpring() { {->
		// TODO Implement runtime spring config (optional)

		def application = grailsApplication
		def config = application.config

		def catchAllMapping = ["/*".toString()]

		ClassLoader classLoader = application.classLoader
		Class registrationBean = ClassUtils.isPresent("org.springframework.boot.web.servlet.FilterRegistrationBean", classLoader ) ?
			ClassUtils.forName("org.springframework.boot.web.servlet.FilterRegistrationBean", classLoader) :
			ClassUtils.forName("org.springframework.boot.context.embedded.FilterRegistrationBean", classLoader)

		permalinkFilter(registrationBean) {
			filter = new PermalinkFilter()
			urlPatterns = catchAllMapping
			order = Ordered.LOWEST_PRECEDENCE - 100
		}
	}
	}

	void doWithDynamicMethods() {
		// TODO Implement registering dynamic methods to classes (optional)
	}

	void doWithApplicationContext() {
		// TODO Implement post initialization spring config (optional)
	}

	void onChange(Map<String, Object> event) {
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
	}

	void onConfigChange(Map<String, Object> event) {
		// TODO Implement code that is executed when the project configuration changes.
		// The event is the same as for 'onChange'.
	}

	void onShutdown(Map<String, Object> event) {
		// TODO Implement code that is executed when the application shuts down (optional)
	}

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
