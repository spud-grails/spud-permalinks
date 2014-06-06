package spud.permalinks


import grails.util.GrailsNameUtils
import org.hibernate.criterion.CriteriaSpecification
import grails.plugin.cache.CacheEvict
import grails.plugin.cache.Cacheable

class SpudPermalinkService {
  static transactional = false
  def permalinkForUrl(url, siteId=0) {
    def permalink = SpudPermalink.findBySiteIdAndUrlName(siteId,url)
    return permalink
  }

  def permalinksForObject(attachment,siteId=0) {
  	def objectType =  GrailsNameUtils.getShortName(attachment.class)
  	def objectId   = attachment.id

  	def permalinks = SpudPermalink.createCriteria().list {
      eq('siteId', siteId)
  		eq('attachmentType', objectType)
  		eq('attachmentId', objectId)
  	}

  	return permalinks
  }


  @CacheEvict(value='spud.permalinks.site', allEntries=true)
  def createPermalink(url, attachment, destinationUrl,siteId=0) {
  	def objectType =  GrailsNameUtils.getShortName(attachment.class)
  	def objectId   = attachment.id

  	// Clear out any permalinks for the destinationUrl
    SpudPermalink.where{ siteId == siteId && urlName == destinationUrl}.deleteAll()

    // Update old permalinks for attachment to new endpoint
    def objectPermalinks = SpudPermalink.where {  attachmentType == objectType && attachmentId == objectId}.updateAll(destinationUrl: destinationUrl)


  	// Check if permalink already exists
  	def permalink = SpudPermalink.findBySiteIdAndUrlName(siteId,url)

  	if(!permalink) {
			permalink = new SpudPermalink(urlName: url, siteId: siteId)
  	}
  	permalink.attachmentType = objectType
		permalink.attachmentId   = objectId
		permalink.destinationUrl = destinationUrl




		return permalink.save()
  }


  @CacheEvict(value='spud.permalinks.site', allEntries=true)
  void evictCache() {
    log.info("Evicting Permalinks Cache")
  }

  @CacheEvict(value='spud.permalinks.site', allEntries=true)
  def deletePermalinksForAttachment(attachment) {
    def objectType =  GrailsNameUtils.getShortName(attachment.class)
    def objectId   = attachment.id
    SpudPermalink.where {attachmentType == objectType && attachmentId == objectId}.deleteAll()

  }

  @Cacheable('spud.permalinks.site')
  def permalinksForSite(siteId) {
      def permalinks = SpudPermalink.withCriteria(readOnly:true) {
        eq('siteId', siteId)
        resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)

        projections {
          property('siteId','siteId')
          property('urlName','urlName')
          property('destinationUrl','destinationUrl')
        }
      }
  }


}
