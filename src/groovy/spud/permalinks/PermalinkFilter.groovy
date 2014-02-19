package spud.permalinks

import javax.servlet.*
import org.springframework.web.context.support.WebApplicationContextUtils
import grails.util.Environment


class PermalinkFilter implements Filter {
	def permalinkService
	void init(FilterConfig config) throws ServletException {
        def applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.servletContext)
        permalinkService = applicationContext['spudPermalinkService']
    }

    void destroy() {
    }

    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	def permalinks
		def permalinkUri = request.requestURI
		def redirectUrl

		if(request.contextPath && request.contextPath != "/") {
		    permalinkUri = permalinkUri.substring(request.contextPath.size())
		}

		if(permalinkUri.size() > 0) {
    		def siteId = 0

			permalinks = permalinkService.permalinksForSite(siteId)
			if(permalinks) {
				permalinks = permalinks.findAll { 
					if(permalinkUri.startsWith("/") && permalinkUri.size() > 1) {
						it.urlName == permalinkUri || it.urlName == permalinkUri.substring(1)
					} else {
						it.urlName == permalinkUri
					}
				}
			}
		}
		

		if(permalinks) {
		    def permalink = permalinks[0]
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
			response.setHeader('Location', redirectUrl)
			response.status = 301
			response.flushBuffer()
		}

        if (!response.committed) {
            chain.doFilter(request, response)
        }
    }


}