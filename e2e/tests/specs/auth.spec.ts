import {test, expect} from '@playwright/test';
import {LoginPage} from "../pages/LoginPage";
import {RegisterPage} from "../pages/RegisterPage";
import {RegisterCompletePage} from "../pages/RegisterCompletePage";

test('ユーザ登録_正常系', async ({page}) => {

    const loginPage = new LoginPage(page);
    const registerPage = new RegisterPage(page);
    const registerCompletePage = new RegisterCompletePage(page);

    // ログイン画面
    await loginPage.goto();
    await loginPage.clickRegisterLink();

    // 新規登録画面
    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);
    await registerPage.fillRegisterForm(
        'e2e',
        '00000000000',
        `e2e_${Date.now()}@example.com`,
        'aaaaaaaaaaaaaaa',
        'aaaaaaaaaaaaaaa'
    )
    await registerPage.clickRegisterButton();

    // 登録完了画面
    await expect(page).toHaveTitle(/レストラン予約システム - 登録完了/);
    await registerCompletePage.clickLoginLink();

    // ログイン画面
    await expect(page).toHaveTitle(/レストラン予約システム - ログイン/);

});

test('ユーザ登録_異常系_必須項目未入力', async ({page}) => {

    const loginPage = new LoginPage(page);
    const registerPage = new RegisterPage(page);

    // ログイン画面
    await loginPage.goto();
    await loginPage.clickRegisterLink();

    // 新規登録画面
    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);
    await registerPage.clickRegisterButton();

    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);
    await expect(page.getByText("空白は許可されていません").first()).toBeVisible();

});

test('ユーザ登録_異常系_パスワード不一致', async ({page}) => {

    const loginPage = new LoginPage(page);
    const registerPage = new RegisterPage(page);

    // ログイン画面
    await loginPage.goto();
    await loginPage.clickRegisterLink();


    // 新規登録画面
    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);
    await registerPage.fillRegisterForm(
        'e2e',
        '00000000000',
        `e2e_${Date.now()}@example.com`,
        'aaaaaaaaaaaaaaa',
        'aaaaaaaaaaaaaab'
    );
    await registerPage.clickRegisterButton();

    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);
    await expect(page.getByText("パスワードが一致しません")).toBeVisible();

});

test('ログイン_正常系_一般ユーザ', async ({page}) => {

    const loginPage = new LoginPage(page);

    // ログイン画面
    await loginPage.goto();
    await loginPage.fillLoginForm(process.env.TEST_USER_EMAIL ?? '', process.env.TEST_USER_PASSWORD ?? '');
    await loginPage.clickLoginButton();

    // ユーザ用ホーム画面
    await expect(page).toHaveTitle(/レストラン予約システム - ホーム/);

});

test('ログイン_正常系_管理者', async ({page}) => {

    const loginPage = new LoginPage(page);

    // ログイン画面
    await loginPage.goto();
    await loginPage.fillLoginForm(process.env.TEST_ADMIN_EMAIL ?? '', process.env.TEST_ADMIN_PASSWORD ?? '');
    await loginPage.clickLoginButton();

    // 管理者用ホーム画面
    await expect(page).toHaveTitle(/管理者 - レストラン予約システム - ホーム/);

});

test('ログイン_異常系', async ({page}) => {

    const loginPage = new LoginPage(page);

    // ログイン画面
    await loginPage.goto();
    await loginPage.fillLoginForm('a@a.com', '123456789012345');
    await loginPage.clickLoginButton();

    await expect(page).toHaveTitle(/レストラン予約システム - ログイン/);
    await expect(page.getByText("ユーザー認証に失敗しました。未登録の方は新規登録をお願いいたします。")).toBeVisible();

});