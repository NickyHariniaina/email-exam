create table if not exists image_submission
(
    id         varchar
        constraint image_submission_pk primary key,
    file_name  varchar not null,
    email      varchar not null,
    created_at timestamp not null
);
