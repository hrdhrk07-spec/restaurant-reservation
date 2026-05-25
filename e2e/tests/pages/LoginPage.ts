import {Page} from '@playwright/test';

export class LoginPage {
    constructor(private page: Page) {
    }

    async goto() {
        await this.page.goto('/login');
    }

    async clickRegisterLink() {
        await this.page.getByRole('link', {name: '新規登録はこちら'}).click();
    }

    async fillLoginForm(email: string, password: string) {
        await this.page.locator('#email').fill(email);
        await this.page.locator('#password').fill(password);
    }

    async clickLoginButton() {
        await this.page.getByRole('button', {name: 'ログイン'}).click();
    }

}