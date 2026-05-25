import {test, expect} from '@playwright/test';
import dotenv from 'dotenv';
dotenv.config();

test('ユーザ登録_正常系', async ({page}) => {

    // ログイン画面
    await page.goto('/login');
    await page.getByRole('link', { name: '新規登録はこちら' }).click();

    // 新規登録画面
    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);
    await page.locator('#name').fill('e2e');
    await page.locator('#phoneNumber').fill('00000000000');
    await page.locator('#email').fill(`e2e_${Date.now()}@example.com`);
    await page.locator('#password').fill('aaaaaaaaaaaaaaa');
    await page.locator('#passwordConfirm').fill('aaaaaaaaaaaaaaa');
    await page.getByRole('button', { name: 'アカウント作成' }).click();

    // 登録完了画面
    await expect(page).toHaveTitle(/レストラン予約システム - 登録完了/);
    await page.getByRole('link', { name: 'ログインはこちら' }).click();

    // ログイン画面
    await expect(page).toHaveTitle(/レストラン予約システム - ログイン/);

});

test('ユーザ登録_異常系_必須項目未入力', async ({page}) => {

    // ログイン画面
    await page.goto('/login');
    await page.getByRole('link', { name: '新規登録はこちら' }).click();
    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);

    // 新規登録画面
    await page.getByRole('button', { name: 'アカウント作成' }).click();

    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);
    await expect(page.getByText("空白は許可されていません").first()).toBeVisible();

});

test('ユーザ登録_異常系_パスワード不一致', async ({page}) => {

    // ログイン画面
    await page.goto('/login');
    await page.getByRole('link', { name: '新規登録はこちら' }).click();
    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);

    // 新規登録画面
    await page.locator('#name').fill('e2e');
    await page.locator('#phoneNumber').fill('00000000000');
    await page.locator('#email').fill(`e2e_${Date.now()}@example.com`);
    await page.locator('#password').fill('aaaaaaaaaaaaaaa');
    await page.locator('#passwordConfirm').fill('aaaaaaaaaaaaaab');
    await page.getByRole('button', { name: 'アカウント作成' }).click();

    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);
    await expect(page.getByText("パスワードが一致しません")).toBeVisible();

});

test('ログイン_正常系_一般ユーザ', async ({page}) => {

    // ログイン画面
    await page.goto('/login');
    await page.locator('#email').fill(process.env.TEST_USER_EMAIL ?? '');
    await page.locator('#password').fill(process.env.TEST_USER_PASSWORD ?? '');
    await page.getByRole('button', { name: 'ログイン' }).click();

    // ユーザ用ホーム画面
    await expect(page).toHaveTitle(/レストラン予約システム - ホーム/);

});

test('ログイン_正常系_管理者', async ({page}) => {

    // ログイン画面
    await page.goto('/login');
    await page.locator('#email').fill(process.env.TEST_ADMIN_EMAIL ?? '');
    await page.locator('#password').fill(process.env.TEST_ADMIN_PASSWORD ?? '');
    await page.getByRole('button', { name: 'ログイン' }).click();

    // 管理者用ホーム画面
    await expect(page).toHaveTitle(/管理者 - レストラン予約システム - ホーム/);

});

test('ログイン_異常系', async ({page}) => {

    // ログイン画面
    await page.goto('/login');
    await page.locator('#email').fill('a@a.com');
    await page.locator('#password').fill('123456789012345');
    await page.getByRole('button', { name: 'ログイン' }).click();

    await expect(page).toHaveTitle(/レストラン予約システム - ログイン/);
    await expect(page.getByText("ユーザー認証に失敗しました。未登録の方は新規登録をお願いいたします。")).toBeVisible();

});