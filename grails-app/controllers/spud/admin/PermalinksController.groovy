package spud.admin
import spud.permalinks.*
import  spud.core.*
import  spud.security.*

@SpudApp(name="Permalinks", thumbnail="spud/admin/permalinks_thumb.png",order="90")
@SpudSecure(['PERMALINKS'])
class PermalinksController {
	static namespace = 'spud_admin'
	def spudPermalinkService
	def spudMultiSiteService

	def index = {
		def permalinks = SpudPermalink.createCriteria().list([max:25] + params) {
			eq('siteId',spudMultiSiteService.activeSite.siteId)
		}
		render view: '/spud/admin/permalinks/index', model:[permalinks: permalinks, permalinkCount: SpudPermalink.count()]
	}

	def create = {
		def permalink = new SpudPermalink()
		render view: '/spud/admin/permalinks/create', model: [permalink: permalink]
	}

	def save = {
		if(!params.permalink) {
			flash.error = "Permalink Submission not specified"
			return
		}

		def permalink = new SpudPermalink(params.permalink)
		permalink.siteId = spudMultiSiteService.activeSite.siteId

		if(permalink.save(flush:true)) {
			spudPermalinkService.evictCache()
			redirect(resource: 'permalinks', action: 'index', namespace: 'spud_admin')
		} else {
			flash.error = "Error saving permalink"
			render view: '/spud/admin/permalinks/create', model:[permalink: permalink]
		}
	}

	def edit = {
		def permalink = loadPermalink()

		if(!permalink) {
			return
		}

		render view: '/spud/admin/permalinks/edit', model: [permalink: permalink]
	}

	def update = {
		if(!params.permalink) {
			flash.error = "Permalink Submission not specified"
			redirect resource: 'permalinks', action: 'index', namespace: 'spud_admin'
			return
		}

		def permalink = loadPermalink()
		if(!permalink) {
			return
		}

		params.permalink.each { param ->
			if(param.key != 'siteId') {
				permalink."${param.key}" = param.value
			}
		}

		permalink.siteId = spudMultiSiteService.activeSite.siteId

		if(!permalink.save(flush:true)) {
			flash.error = "Error Saving Permalink"
			render view: '/spud/admin/permalinks/edit', model: [permalink: permalink]
			return
		}
		spudPermalinkService.evictCache()
		redirect resource: 'permalinks', action: 'index', namespace: 'spud_admin'
	}


	def delete = {
		def permalink = loadPermalink()

		if(!permalink) {
			return
		}
		permalink.delete(flush:true)
		spudPermalinkService.evictCache()
		redirect resource: 'permalinks', action: 'index', namespace: 'spud_admin'
	}

	private loadPermalink() {

		if(!params.id) {
			flash.error = "Permalink Submission not specified"
			redirect resource: 'permalink', action: 'index', namespace: 'spud_admin'
			return null
		}

		def siteId = spudMultiSiteService.activeSite.siteId
		def permalink = SpudPermalink.findBySiteIdAndId(siteId,params.id)
		if(!permalink) {
			flash.error = "Permalink not found!"
			redirect resource: 'permalinks', action: 'index', namespace: 'spud_admin'
			return null
		}
		return permalink
	}


}
