package ru.netology.nmedia.dto

import java.io.File

data class Media(val id: String) //в качестве результата от сервера (Ctrl+B на ф-ции сохранения на сервере), перенесли в dto

data class MediaUpload(val file: File)
