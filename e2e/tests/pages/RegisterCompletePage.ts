import {Page} from '@playwright/test';

export class RegisterCompletePage {
    constructor(private page: Page) {
    }

    async clickLoginLink() {
        await this.page.getByRole('link', {name: 'ログインはこちら'}).click();
    }
}