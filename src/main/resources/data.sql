insert into Users(username, name, surname, phone, email, encoded_password,account_non_expired, account_non_locked, credentials_non_expired, enabled)
            values('Admin', 'admin', 'admin', '+79602574201', 'admin@yandex.ru', 'admin', 1, 1, 1, 1);
insert into Roles(user_id, role ) values(1, 'ADMIN');
