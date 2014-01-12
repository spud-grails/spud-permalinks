package spud.permalinks

import grails.transaction.Transactional
import grails.util.GrailsNameUtils

@Transactional
class SpudPermalinkService {
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

  def deletePermalinksForAttachment(attachment) {
    def objectType =  GrailsNameUtils.getShortName(attachment.class)
    def objectId   = attachment.id
    SpudPermalink.where {attachmentType == objectType && attachmentId == objectId}.deleteAll()

  }

}
