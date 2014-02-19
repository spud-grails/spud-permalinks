Spud Permalinks
===============

The Spud Permalinks plugin provides url redirection support for the spud platform with a clean admin interface. With this plugin, urls can be dynamically defined for redirection. This plugin also supports polymorphic attachments from other plugins or your app. An example would be the `spud-cms` plugin which automatically adjusts the permalinks list as pages are renamed. This is great when migrating an old site to a new site and needing to 301 permanently redirect old urls.

Features: 
* Dynamic URL Redirection (via ServletFilter)
* Implements Cache Plugin for caching permalinks list ( High Performance )

Installation/Usage
------------------

To install simply add the spud-permalinks plugin to your BuildConfig:

```groovy
plugins {
  compile ':spud-permalinks:0.1.0'
}
```

You can programatically use the `spudPermalinkService` to define permalinks for other domain objects:


```groovy
def page = SpudPage.get(1)
spudPermalinkService.createPermalink('/oldurl', page, '/newurl')
```

You can also remove all permalinks for an attachment via:

```groovy
spudPermalinkService.deletePermalinksForAttachment(page)
```

To add your own permalinks dynamically simply use the "Permalinks" dashboard app in the `/spud/admin` panel.

**NOTE:**
If you find yourself needing to evict the permalink cache from manually using the `SpudPermalink` domain you can do so with the evictCache Method:

```groovy
spudPermalinkService.evictCache()
```
