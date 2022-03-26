package com.example.mysocialandroidapp.samples

import com.example.mysocialandroidapp.dto.*
import com.example.mysocialandroidapp.enumeration.EventType

object Samples {

    fun getPosts(): List<Post>{
        val posts = listOf(
            getSamplePost(1, 1001, "author1", "Мой дядя1 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mentionIds = mutableSetOf(1001, 1002, 1003), mentionedMe = false,
                likeOwnerIds = setOf(1001, 1002, 1003), likedByMe = true),
            getSamplePost(2, 1002, "author2", "Мой дядя2 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(3, 1003, "author3", "Мой дядя3 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(4, 1001, "author1", "Мой дядя4 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mentionIds = mutableSetOf(1002, 1003), mentionedMe = false,
                likeOwnerIds = setOf(1001, 1003), likedByMe = true),
            getSamplePost(5, 1002, "author2", "Мой дядя5 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(6, 1003, "author3", "Мой дядя6 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(7, 1001, "author1", "Мой дядя7 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, likeOwnerIds = setOf(1002, 1003), likedByMe = false),
            getSamplePost(8, 1002, "author2", "Мой дядя8 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(9, 1003, "author3", "Мой дядя9 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(10, 1001, "author1", "Мой дядя10 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(11, 1002, "author2", "Мой дядя11 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(12, 1003, "author3", "Мой дядя12 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(13, 1001, "author1", "Мой дядя13 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(14, 1002, "author2", "Мой дядя14 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(15, 1003, "author3", "Мой дядя15 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(16, 1001, "author1", "Мой дядя16 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(17, 1002, "author2", "Мой дядя17 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(18, 1003, "author3", "Мой дядя18 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
            getSamplePost(19, 1001, "author1", "Мой дядя19 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), mentionedMe = false, emptySet(), likedByMe = false),
        )
        return posts
    }


    private fun getSamplePost(
        id: Long,
        authorId: Long,
        author: String,
        content: String,
        mentionIds: MutableSet<Long> = mutableSetOf(),
        mentionedMe: Boolean,
        likeOwnerIds: Set<Long> = emptySet(),
        likedByMe: Boolean,
    ) : Post {
        return Post(
            id,
            authorId,
            author,
            "",
            content,
            "01.01.2022",
            likedByMe,
            Coordinates(59.9339, 30.3065),
            "http://ya.ru",
            mentionIds,
            mentionedMe,
            likeOwnerIds,
            null
        )
    }

    fun getWall(authorId: Long): List<Post>{
        return getPosts().filter { x -> x.authorId == authorId }
    }

    fun getJobs(authorId: Long): List<Job>{
        var jobs = mapOf<Long, List<Job>>(
            Pair(1001, listOf(
                getSampleJob(10001, "job1", "position1"),
                getSampleJob(10002, "job2", "position2"),
                getSampleJob(10003, "job3", "position3"),
            )),
            Pair(1002, listOf(
                getSampleJob(10004, "job4", "position4"),
                getSampleJob(10005, "job5", "position5"),
                getSampleJob(10006, "job6", "position6"),
                getSampleJob(10007, "job7", "position7"),
            )),
            Pair(1003, listOf(
                getSampleJob(10008, "job8", "position8"),
                getSampleJob(10009, "job9", "position9"),
            )),
        )
        return if (jobs.containsKey(authorId))
            jobs[authorId]!!
        else
            emptyList()
    }

    fun getSampleJob(
        id: Long,
        name: String,
        position: String,
    ) : Job {
        return Job(id, name, position, 12345, 67890, "http://ya.ru")
    }

    fun getUsers() : List<User>{
        return listOf(
            User(1001, "login1", "author1", null, emptyList(),),
            User(1002, "login2", "author2", null, emptyList(),),
            User(1003, "login3", "author3", null, emptyList(),),
        )
    }

    private fun getSampleEvent(
        id: Long,
        authorId: Long,
        author: String,
        content: String,
        likeOwnerIds: Set<Long> = emptySet(),
        likedByMe: Boolean,
        speakerIds: MutableSet<Long> = mutableSetOf(),
        participantIds: MutableSet<Long> = mutableSetOf(),
        participatedByMe: Boolean,
    ) : Event {
        return Event(
            id,
            authorId,
            author,
            "",
            content,
            "01.01.2022",
            "01.01.2022",
            Coordinates(59.9339, 30.3065),
            EventType.OFFLINE,
            likeOwnerIds,
            likedByMe,
            speakerIds,
            participantIds,
            participatedByMe,
            null,
            "http://ya.ru",
        )
    }

    fun getEvents() : List<Event>{
        val events = listOf(
            getSampleEvent(1, 1001, "author1", "Мой дядя1 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                likeOwnerIds = setOf(1001, 1002, 1003), likedByMe = true,
                speakerIds = mutableSetOf(1001, 1002),
                participantIds = mutableSetOf(1001, 1002), participatedByMe = true
            ),
            getSampleEvent(2, 1002, "author2", "Мой дядя2 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                likeOwnerIds = setOf(), likedByMe = false,
                speakerIds = mutableSetOf(1002),
                participantIds = mutableSetOf(1002), participatedByMe = false
            ),
            getSampleEvent(3, 1003, "author3", "Мой дядя3 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                likeOwnerIds = setOf(1003), likedByMe = false,
                speakerIds = mutableSetOf(1001, 1002),
                participantIds = mutableSetOf(1002), participatedByMe = false
            ),
            getSampleEvent(4, 1001, "author1", "Мой дядя4 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(5, 1002, "author2", "Мой дядя5 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(6, 1003, "author3", "Мой дядя6 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(7, 1001, "author1", "Мой дядя7 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(8, 1002, "author2", "Мой дядя8 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(9, 1003, "author3", "Мой дядя9 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(10, 1001, "author1", "Мой дядя10 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(11, 1002, "author2", "Мой дядя11 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(12, 1003, "author3", "Мой дядя12 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(13, 1001, "author1", "Мой дядя13 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(14, 1002, "author2", "Мой дядя14 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(15, 1003, "author3", "Мой дядя15 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(16, 1001, "author1", "Мой дядя16 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(17, 1002, "author2", "Мой дядя17 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(18, 1003, "author3", "Мой дядя18 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
            getSampleEvent(19, 1001, "author1", "Мой дядя19 самых честных правил, Когда не в шутку занемог, Он уважать себя заставил И лучше выдумать не мог.",
                mutableSetOf(), false,
                mutableSetOf(), mutableSetOf(), false),
        )
        return events
    }
}