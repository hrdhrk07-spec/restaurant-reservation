import {Page} from '@playwright/test';

export class RegisterPage {
    constructor(private page: Page) {
    }

    async fillRegisterForm(name: string, phoneNumber: string, email: string, password: string, passwordConfirm: string) {
        await this.page.locator('#name').fill(name);
        await this.page.locator('#phoneNumber').fill(phoneNumber);
        await this.page.locator('#email').fill(email);
        await this.page.locator('#password').fill(password);
        await this.page.locator('#passwordConfirm').fill(passwordConfirm);
    }

    async clickRegisterButton() {
        await this.page.getByRole('button', { name: 'アカウント作成' }).click();
    }
}