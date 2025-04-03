package frc.robot

object HttpLimelightService : LimelightService {
    private val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofMillis(Constants.LimelightConstants.TIMEOUT))
        .build()

        override suspend fun fetchResults(): String? {
            val result: String?
            try {
                val response = withContext(Dispatchers.IO) {
                    client.send(
                        HttpRequest.newBuilder()
                        .uri(URL("${Constants.LimelightConstants.IP_ADDR}/results").toURI())
                        .timeout(Duration.ofMillis(Constants.LimelightConstants.TIMEOUT))
                        .GET()
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                    )
                }
                result = response.body()
            } catch (e: Exception) {
                print(e)
                result = null
            }
            return result
        }
}