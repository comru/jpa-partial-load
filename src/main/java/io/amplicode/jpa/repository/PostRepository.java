package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.Post;
import io.amplicode.jpa.projection.*;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Transactional(readOnly = true)
    <T> List<T> findAllByTitleContainsIgnoreCase(String tittle, Class<T> projection);

    @Transactional(readOnly = true)
    @Query("""
            select a from Post a
            left join fetch a.author
            left join fetch a.likeUsers""")
    List<Post> findAllWithAssociations();

    //--------- basic load -----------
    @Query("""
            select a.id as id, a.slug as slug, a.title as title from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<PostBasic> findAllPostBase(String tittle);

    @Query("""
            select new io.amplicode.jpa.projection.PostBasicDto(a.id, a.slug, a.title) from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<PostBasicDto> findAllPostBasicDto(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Tuple> findAllTupleBasic(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Map<String, Object>> findAllMapBasic(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<List<Object>> findAllListBasic(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Object[]> findAllObjectArrayBasic(String tittle);

    //--------- to one load -----------
    @Query("""
            select a.id as id, a.slug as slug, a.title as title, a.author.id as authorId, a.author.username as authorUsername from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<PostWithAuthorFlat> findAllPostWithAuthorFlat(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, new io.amplicode.jpa.projection.UserPresentationDto(a.author.id, a.author.username) from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<PostWithAuthorClass> findAllPostWithAuthorClass(String tittle);

    @Query("""
            select new io.amplicode.jpa.projection.PostWithAuthorFlatDto(a.id, a.slug, a.title, a.author.id, a.author.username) from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<PostWithAuthorFlatDto> findAllPostWithAuthorFlatDto(String tittle);

    @Query("""
            select new io.amplicode.jpa.projection.PostWithAuthorNestedDto(
                a.id,
                a.slug,
                a.title,
                new io.amplicode.jpa.projection.UserPresentationDto(a.author.id, a.author.username)) from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<PostWithAuthorNestedDto> findAllPostWithAuthorNestedDto(String tittle);

    @Query("""
            select new io.amplicode.jpa.projection.PostWithAuthorNestedMap(
                a.id,
                a.slug,
                a.title,
                new map(a.author.id as id, a.author.username as username)) from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<PostWithAuthorNestedMap> findAllPostWithAuthorNestedMap(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, a.author.id as authorId, a.author.username as authorUsername from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Tuple> findAllTupleWithAuthor(String tittle);


    @Query("""
            select a.id as id, a.slug as slug, a.title as title, a.author.id as authorId, a.author.username as authorUsername from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Object[]> findAllObjectWithAuthor(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, a.author.id as authorId, a.author.username as authorUsername from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<List<?>> findAllListWithAuthor(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, a.author.id as authorId, a.author.username as authorUsername from Post a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Map<String, Object>> findAllMapWithAuthor(String tittle);

    //--------- to many load -----------

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, lu.id as likeUsersId, lu.username as likeUsersUsername from Post a
            left join a.likeUsers lu
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<PostWithLikeUsersFlat> findInterfaceFlatPrj(String tittle);

    @Query("""
            select new io.amplicode.jpa.projection.PostWithLikeUsersDto(a.id, a.slug, a.title, lu.id, lu.username) from Post a
            left join a.likeUsers lu
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<PostWithLikeUsersDto > findToManyClassFlat(String tittle);

    @Query("""
            select new io.amplicode.jpa.projection.PostWithLikeUsersNestedDto(
                a.id,
                a.slug,
                a.title,
                new io.amplicode.jpa.projection.UserPresentationDto(lu.id, lu.username)
            ) from Post a
            left join a.likeUsers lu
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<PostWithLikeUsersNestedDto> findToManyClassNested(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, lu.id as likeUserId, lu.username as likeUserUsername from Post a
            left join a.likeUsers lu
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Tuple> findToManyTuple(String tittle);
}