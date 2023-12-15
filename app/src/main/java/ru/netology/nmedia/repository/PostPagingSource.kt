package ru.netology.nmedia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.toEntity

class PostPagingSource(private val postsApiService: PostsApiService): PagingSource<Long, Post>() {

    override fun getRefreshKey(state: PagingState<Long, Post>): Long? {
        return null
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {

        try {
            val response = when (params) {
                is LoadParams.Refresh -> {
                    postsApiService.getLatest(params.loadSize)
                }

                is LoadParams.Append -> {
                    postsApiService.getBefore(id = params.key, count = params.loadSize)
                }

                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(), nextKey = null, prevKey = params.key
                )
            }

            if (!response.isSuccessful) {
                //throw ApiError(response.code(), response.message())
                throw HttpException(response)
            }

            val data = response.body().orEmpty()
            return LoadResult.Page(
                data = data,
                prevKey = params.key,
                nextKey = data.lastOrNull()?.id
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }
}