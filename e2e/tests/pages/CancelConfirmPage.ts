import {Page} from '@playwright/test';

export class CancelConfirmPage {
    constructor(private page: Page) {
    }

    async clickCancelButton() {
        await this.page.getByRole('button', {name: '予約キャンセル'}).click();
    }

}