INSERT INTO public.users(
    id, created_at, updated_at, email, name, password, phone_number, role, failed_login_attempts)
VALUES (nextval('users_id_seq'), now(), now(), 'test_user@example.com', 'E2Eテスト用ユーザ', '$2y$10$W6SWLtOPWydWr6kGO5WgFO2KSsUHGPH353tFEag0LhAlRcAkqyRna', '00000000000', 'USER', 0)
ON CONFLICT (email) DO NOTHING;

INSERT INTO public.users(
    id, created_at, updated_at, email, name, password, phone_number, role, failed_login_attempts)
VALUES (nextval('users_id_seq'), now(), now(), 'test_admin@example.com', 'E2Eテスト用管理者', '$2y$10$JOi.tOIBkZ4zn0Q4oN/NBuwApqI5HYrVPfOaTPMi0.deZgUWtRlLu', '00000000000', 'ADMIN', 0)
ON CONFLICT (email) DO NOTHING;