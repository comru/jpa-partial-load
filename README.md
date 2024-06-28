## Все способы частичной загрузки JPA сущностей в Spring приложении

### Содержание
- [О проекте](#о-проекте)
- [Задача](#задача)
- [Тестовые данные](#тестовые-данные)
- [Spring Data Repository](#spring-data-repository)
- [EntityManger](#entitymanger)
- [Criteria Builder](#criteria-builder)
- [Jakarta Data](#jakarta-data)
- [Getting Started](#getting-started)
- [Built With](#built-with)

### О проекте
Существует множество способов взаимодействия с реляционными БД в Java приложениях и в Spring в частности. Самым популярным способом, остается JPA и его основная реализация Hibernate, именно на эту реализацию мы будем опираться в нашем проекте. 
Одной из основных претензий к JPA, остается большой объем выгружаемых данныx, т.е. вместо того чтобы загрузить несколько атрибутов из сущности которые на нужны, мы загружаем всю сущность целиком. Часто это происходит из-за не знания, что можно указать какие конкретно поля мы хотим загрузить. Иногда это происходит, просто потому что так легче. Давайте попробуем рассмотреть все способы такой частичной загрузки сущностей. Рассмотрим на примере основых способов взаимодействия с Hibernate в Spring приложениях:
- Spring Data JPA 
- EntityManager
- Criteria API 

### Задача
В качестве модели данных возьмем модель из популярного приложения для демо [realworld conduit](https://realworld-docs.netlify.app/docs/intro): 
```plantuml
entity Article {
    id: bigint,
    slug: text,
    title: text,
    body: text,
    createdAt: timestamp,
    updatedAt: timestamp
}
```
Наша задача для каждого способа частичной загрузки попробовать загрузить следующие данные:
1. Несколько базовый полей из сущности Author - id, slug, tittle. 
2. Несколько базовый полей + ToOne ассоциация. В нашем случае это author: User - id, username 
3. Несколько базовый полей + ToMany ассоциация. У нас есть два атрибута c ассоциацией *ToMany, будем загружать favorited(лайкнувших пользователей), также будем загружать id, username. 
4. Embedded (после)

Запрос буде простой - найти все статьи заголовок которых содержит заданный текст.

Проверять результат мы будем в соответсвующих тестах, результат запроса будут видны в консоли. 

### Тестовые данные
Создадим одну сущность Article c двумя подписчиками и двум тегами. 

### Spring Data Repository
 
1. Derived method + [Interface-based Projections](https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html)
    - nested projection работают не оптимизировано https://github.com/spring-projects/spring-data-jpa/issues/3352
    - Проекция это прокси над самой сущностью
2. Derived method + [Class-based Projections (DTOs)](https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projections.dtos)
3. Query method + Interface-based Projections (оно вообще работает?)
   - Проекция это прокси над org.springframework.data.jpa.repository.query.AbstractJpaQuery.TupleConverter.TupleBackedMap
4. Query method + Class-based Projections
   - Только через конструктор, но за то работают nested
   - Хотя можно написать Spring Converter и должно заработать 
5. Query method + Tuple
   - Не работает query без алиасов, точнее работает, но не получается обратиться по имени атрибута
   - ToMany просто дублирует строки
6. Query method + Object Array
   - да тоже самое, что и tuple
7. Query method + Entity, вообще не работает 
8. Specification и проблемы с EntityGraph и Projection
   - Не поддерживается  EntityGraph
   - Не поддерживаются Projections  
   - Не поддерживаются Slice

|                                                 | Basic attributes | ToOne            | ToMany                        |
|-------------------------------------------------|------------------|------------------|-------------------------------|
| Derived method + Interface-based Projections    | +                | - Nested, + Flat | - Nested, - Flat (Невозможно) |
| Derived method + Class-based Projections (DTOs) | +                | +                | - Не возможно                 |
| Query method + Interface-based Projections      | + ()             | -                | - Не возможно                 |
| Query method + Class-based Projections          | +                | + Nested, + Flat | - Не возможно                 |
| Query method + Tuple                            | +                | +                | Duplicate tuples              |
| Query method + Object Array                     | +                | +                | Duplicate tuples              |


### EntityManger

1. Dto constructor
```sql
select new io.amplicode.jpa.OwnerDto(id, firstName, lastName) from Owner
```
2. Only Return type without constructor
```java
List<OwnerDto> results = em.createQuery(
						"select id, firstName, lastName from Owner", OwnerDto.class)
				.getResultList();
```
3. Object Array
4. Tuple
5. Native query ? 
6. result transformer ?

### Criteria Builder
1. DTO 
2. Tuple
3. Object array

### Jakarta Data

### Built With
- Spring Boot
- Java 21
- Spring Data JPA
- Hibernate
- H2
- Junit


### Getting Started
1. Clone project
2. Go to project directory
3. Run project tests
```shell
./gradlew test
```
