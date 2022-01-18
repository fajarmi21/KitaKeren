package fba.abadi.bahtera.fajar.kotlin.kitakeren.model

import java.util.*
import kotlin.collections.ArrayList

class ImageModel {

    private var image_drawable: Int = 0

    fun getImage_drawables(): Int {
        return image_drawable
    }

    fun setImage_drawables(image_drawable: Int) {
        this.image_drawable = image_drawable
    }
}

class dashboard(val id_dashboard: String, val nama_dashboard: language, val icon_dashboard: String)
class DDashboard(val id_detailDashboard: String, val id_dashboard: String, val nama_detailDashboard: language, val icon_detailDashboard: String)
class Deskripsi(val id_deskripsi: String, val id_detailDashboard: String, val nama_deskripsi: language, val icon_deskripsi: String)
class DDeskripsi(
    val id_detailDeskripsi: String,
    val id_dashboard: String,
    val alamat_detailDeskripsi: String,
    val nomor_detailDeskripsi: String,
    val harga_detailDeskripsi: String,
    val buka_detailDeskripsi: String,
    val pemandu_detailDeskripsi: String,
    val deskripsi_detailDeskripsi: language,
    val foto_detailDeskripsi: ArrayList<String>,
    val nama_deskripsi: language
)
class berita(val judul_news: String, val news: String, val link: String, val created_at: String, val updated_at: String)
class language(val ind: String, val en: String)
class status(val status: String, val message: String)
class rating(
    val id_rating: String,
    val id_user: String,
    val id_detailDeskripsi: String,
    val rating: String,
    val comment: String,
    val foto_rating: String,
    val created_rating: String,
    val updated_rating: String,
    val deleted_rating: String,
    val username: String,
    val email: String,
    val id_dashboard: String,
    val alamat_detailDeskripsi: String,
    val nomor_detailDeskripsi: String,
    val harga_detailDeskripsi: String,
    val buka_detailDeskripsi: String,
    val pemandu_detailDeskripsi: String,
    val deskripsi_detailDeskripsi: language,
    val foto_detailDeskripsi: language
)
class foto(
    val id: List<Ds00011>
)

class Ds00011(
    val initialPreview: List<String>,
    val initialPreviewConfig: List<InitialPreviewConfig>
)

class InitialPreviewConfig(
    val key: Int,
    val caption: String,
    val size: Int,
    val downloadUrl: String,
    val url: String
)
