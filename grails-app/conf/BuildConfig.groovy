grails.project.work.dir = 'target'
grails.project.target.level = 1.6
grails.project.source.level = 1.6

grails.project.fork = [
    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    inherits "global"
    log "warn"
    repositories {
        grailsCentral()
        grailsPlugins()
        mavenCentral()
    }

    plugins {
        runtime ":spud-core:0.5.1"
        runtime ":cache:1.1.7"
        compile ':webxml:1.4.1'

        runtime(':hibernate4:4.3.5.4') {
            export = false
        }

        build(":release:3.0.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }
    }
}

// grails.plugin.location."spud-core" = "../spud-core"
