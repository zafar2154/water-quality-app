import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class SensorResponse(val ph: Float?, val tds: Float?, val temperature: Float?)

object ApiClient {
    private const val BASE_URL = "http://192.168.1.7"

    val retrofit: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {
    @GET("sensor")
    suspend fun getUser(): SensorResponse
}