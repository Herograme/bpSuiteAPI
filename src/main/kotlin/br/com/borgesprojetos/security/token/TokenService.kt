package br.com.borgesprojetos.security.token

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims:TokenClaim
    ): String
}