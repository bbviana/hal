package br.com.viana.hal.money

import org.junit.Test

/**
 * @author bbviana
 */
class SantanderSpec {

    @Test
    void fetch(){
        def config = new Settings()
        config.read()

        SantanderFetcher fetcher = new SantanderFetcher(
                config: config
        )

        fetcher.fetch()
    }
}
