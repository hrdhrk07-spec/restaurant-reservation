INSERT INTO public.users(
    id, created_at, updated_at, email, name, password, phone_number, role, failed_login_attempts)
VALUES (nextval('users_id_seq'), now(), now(), 'test_user@example.com', 'E2Eテスト用ユーザ', '$2y$10$W6SWLtOPWydWr6kGO5WgFO2KSsUHGPH353tFEag0LhAlRcAkqyRna', '00000000000', 'USER', 0)
ON CONFLICT (email) DO NOTHING;

INSERT INTO public.users(
    id, created_at, updated_at, email, name, password, phone_number, role, failed_login_attempts)
VALUES (nextval('users_id_seq'), now(), now(), 'test_admin@example.com', 'E2Eテスト用管理者', '$2y$10$JOi.tOIBkZ4zn0Q4oN/NBuwApqI5HYrVPfOaTPMi0.deZgUWtRlLu', '00000000000', 'ADMIN', 0)
ON CONFLICT (email) DO NOTHING;

INSERT INTO public.restaurants(
    id, created_at, updated_at, cuisine_type, image_path, location, name, reception_end_time, reception_start_time)
VALUES (1, now(), now(), '和食', 'test.jpg', '大阪府大阪市北区1-1-1', '和心', '22:00:00', '17:00:00')
    ON CONFLICT (id) DO NOTHING;
SELECT setval('restaurants_id_seq', (SELECT MAX(id) FROM restaurants));

INSERT INTO public.seat_details(
    id, created_at, updated_at, duration, number_of_seats, person_per_seat, restaurant_id)
VALUES (1, now(), now(), 120, 1, 2, 1)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO public.seat_details(
    id, created_at, updated_at, duration, number_of_seats, person_per_seat, restaurant_id)
VALUES (2, now(), now(), 150, 99, 4, 1)
    ON CONFLICT (id) DO NOTHING;
SELECT setval('seat_details_id_seq', (SELECT MAX(id) FROM seat_details));

INSERT INTO public.holidays(
    id, created_at, updated_at, holiday_day_of_week, restaurant_id)
VALUES (1, now(), now(), 'WEDNESDAY', 1)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO public.holidays(
    id, created_at, updated_at, holiday_day_of_week, restaurant_id)
VALUES (2, now(), now(), 'SATURDAY', 1)
    ON CONFLICT (id) DO NOTHING;
SELECT setval('holidays_id_seq', (SELECT MAX(id) FROM holidays));