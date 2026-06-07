import {LoginPage} from "../pages/LoginPage";
import {RegisterPage} from "../pages/RegisterPage";
import {RegisterCompletePage} from "../pages/RegisterCompletePage";
import {expect, Page} from "@playwright/test";

export async function createUser(page: Page): Promise<[string, string]> {

    const loginPage = new LoginPage(page);
    const registerPage = new RegisterPage(page);
    const registerCompletePage = new RegisterCompletePage(page);

    const email = `e2e_${crypto.randomUUID()}@example.com`;
    const password = process.env.TEST_CREATE_USER_PASSWORD ?? '';

    // ログイン画面
    await loginPage.goto();
    await loginPage.clickRegisterLink();

    // 新規登録画面
    await expect(page).toHaveTitle(/レストラン予約システム - 新規登録/);
    await registerPage.fillRegisterForm(
        'e2e',
        '00000000000',
        email,
        password,
        password
    )
    await registerPage.clickRegisterButton();

    // 登録完了画面
    await expect(page).toHaveTitle(/レストラン予約システム - 登録完了/);
    await registerCompletePage.clickLoginLink();

    // ログイン画面
    await expect(page).toHaveTitle(/レストラン予約システム - ログイン/);

    return [email, password];

}
