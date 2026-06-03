import {Page} from '@playwright/test';

export class CancelCompletePage {
    constructor(private page: Page) {
    }

    async clickReservationListButton() {
        await this.page.locator('a.btn-primary', {hasText: '予約一覧'}).click();
    }

}