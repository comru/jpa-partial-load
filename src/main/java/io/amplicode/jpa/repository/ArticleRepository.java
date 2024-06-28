package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.Article;
import io.amplicode.jpa.projection.*;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Transactional(readOnly = true)
    <T> List<T> findAllByTitleContainsIgnoreCase(String tittle, Class<T> projection);

    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"favorited"})
    <T> List<T> findAllWithFavoritedByTitleContainsIgnoreCase(String tittle, Class<T> projection);

    @Transactional(readOnly = true)
    @Query("""
            select a from Article a
            left join fetch a.author
            left join fetch a.tags
            left join fetch a.favorited""")
    List<Article> findAllWithAssociations();

    @Query("""
            select a.id as id, a.slug as slug, a.title as title from Article a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<ArticleBasic> findAllArticleBase(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, a.author.id as authorId, a.author.username as authorUsername from Article a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<ArticleWithAuthorFlat> findAllArticleWithAuthorFlat(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, a.author.id as authorId, a.author.username as authorUsername from Article a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<ArticleWithAuthorNested> findAllArticleWithAuthorNested(String tittle);

    @Query("""
            select new io.amplicode.jpa.projection.ArticleBasicDto(a.id, a.slug, a.title) from Article a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<ArticleBasicDto> findAllArticleBasicDto(String tittle);

    @Query("""
            select new io.amplicode.jpa.projection.ArticleWithAuthorFlatDto(a.id, a.slug, a.title, a.author.id, a.author.username) from Article a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<ArticleWithAuthorFlatDto> findAllArticleWithAuthorFlatDto(String tittle);

    @Query("""
            select new io.amplicode.jpa.projection.ArticleWithAuthorNestedDto(
                a.id,
                a.slug,
                a.title,
                new io.amplicode.jpa.projection.UserPresentationDto(a.author.id, a.author.username)) from Article a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<ArticleWithAuthorNestedDto> findAllArticleWithAuthorNestedDto(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title from Article a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Tuple> findAllTupleBasic(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, a.author.id as authorId, a.author.username as authorUsername from Article a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Tuple> findAllTupleWithAuthor(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, f.username as favoritedUsername from Article a
            left join a.favorited f
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Tuple> findAllWithFavorited(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title from Article a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Object[]> findAllObjectBasic(String tittle);

    @Query("""
            select a.id as id, a.slug as slug, a.title as title, a.author.id as authorId, a.author.username as authorUsername from Article a
            where lower(a.title) like lower(concat('%', ?1, '%'))""")
    List<Object[]> findAllObjectWithAuthor(String tittle);
}