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
			log.debug "doFilterInternal request.contextPath: ${request.contextPath}"
			permalinkUri = permalinkUri.substring(request.contextPath.size())
		}

		if(permalinkUri.size() > 0) {
			def siteId = request.getAttribute('spudSiteId')
			log.debug "doFilter siteId: ${siteId}"
			permalinks = permalinkService.permalinksForSite(siteId)
			if(permalinks) {
				permalinks = permalinks.findAll {
					if(permalinkUri.startsWith("/") && permalinkUri.size() > 1) {
						it.urlName == permalinkUri || it.urlName == permalinkUri.substring(1)
					} else {
						it.urlName == permalinkUri
					}
				}
				log.debug "doFilter permalinks: ${permalinks}"
			}
		}

		if(permalinks) {
			def permalink = permalinks[0]
			log.debug "doFilter permalink: ${permalink}"
			if(!permalink.destinationUrl.startsWith("/")) {
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
				redirectUrl = request.contextPath + permalinks[0].destinationUrl
			}
		}

		if(redirectUrl) {
			log.debug "doFilter redirectUrl: ${redirectUrl}"
			response.setHeader('Location', redirectUrl)
			response.status = 301
			response.flushBuffer()
		}

		if (!response.committed) {
			chain.doFilter(request, response)
		}
	}

	@Override
	void destroy() {

	}
}
