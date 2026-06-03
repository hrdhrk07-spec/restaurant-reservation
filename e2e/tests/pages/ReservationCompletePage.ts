import {expect, Page} from '@playwright/test';

export class ReservationCompletePage {
    constructor(private page: Page) {
    }

    async clickReservationListButton() {
        await this.page.locator('a.btn-primary', {hasText: '予約一覧'}).click();
    }

}