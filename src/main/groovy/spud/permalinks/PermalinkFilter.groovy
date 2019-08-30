package spud.permalinks

import groovy.util.logging.Slf4j
import org.springframework.web.context.support.WebApplicationContextUtils

import javax.servlet.*

@Slf4j
class PermalinkFilter implements Filter {
	def permalinkService
	def applicationContext

	@Override
	void init(FilterConfig filterConfig) throws ServletException {
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.servletContext)
		permalinkService = applicationContext['spudPermalinkService']
	}

	@Override
	void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		def permalinks
		def permalinkUri = request.requestURI
		def redirectUrl

		log.debug "doFilter permalinkUri: ${permalinkUri}"
		log.debug "doFilter redirectUrl: ${redirectUrl}"
		log.debug "doFilter request.contextPath: ${request.contextPath}"
		if(request.contextPath && request.contextPath != "/") {
			log.debug "doFilter request.contextPath: ${request.contextPath}"
			permalinkUri = permalinkUri.substring(request.contextPath.size())
		}
		log.debug "doFilter permalinkUri: ${permalinkUri}"
		if(permalinkUri.size() > 0) {
			def siteId = request.getAttribute('spudSiteId')
			log.debug "doFilter siteId: ${siteId}"
			permalinks = permalinkService.permalinksForSite(siteId)
			log.debug "doFilter permalinks for siteId ${siteId}: ${permalinks}"
			if(permalinks) {
				log.trace "doFilter permalinks was not null and looking for match for ${permalinkUri}"
				permalinks = permalinks.findAll {
					if(permalinkUri.startsWith("/") && permalinkUri.size() > 1) {
						it.urlName == permalinkUri || it.urlName == permalinkUri.substring(1)
					} else {
						it.urlName == permalinkUri
					}
				}
				log.trace "doFilter permalinks after findAll: ${permalinks}"
			}
		} else {
			log.debug "doFilter permalinkUri.size was 0"
		}

		if(permalinks) {
			def permalink = permalinks[0]
			log.debug "doFilter permalink[0]: ${permalink}"
			if(!permalink.destinationUrl.startsWith("/")) {
				log.debug "permalink.destinationUrl: ${permalink.destinationUrl}"
				if(permalink.destinationUrl ==~ /(http|https|ftp)\:\/\/.*/) {
					redirectUrl = permalinks[0].destinationUrl
				} else {
					def prefix = request.contextPath ?: "/"
					if(!prefix.endsWith("/")) {
						prefix += "/"
					}
					redirectUrl =  prefix + permalinks[0].destinationUrl
				}
			} else {
				log.debug "permalink.destinationUrl starts with /"
				redirectUrl = request.contextPath + permalinks[0].destinationUrl
			}
		} else {
			log.debug "doFilter permalinks was null"
		}

		if(redirectUrl) {
			log.debug "doFilter redirectUrl: ${redirectUrl}"
			response.setHeader('Location', redirectUrl)
			response.status = 301
			response.flushBuffer()
		} else {
			log.debug "redirectUrl was null"
		}

		if (!response.committed) {
			log.trace "doFilter request: ${request}"
			chain.doFilter(request, response)
		} else {
			log.trace "doFilter response.committed: ${response.committed}"
		}
	}

	@Override
	void destroy() {

	}
}
