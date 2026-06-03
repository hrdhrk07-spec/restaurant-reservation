import {Page} from '@playwright/test';

export class ReservationConfirmPage {
    constructor(private page: Page) {
    }

    async clickReservationButton() {
        await this.page.getByRole('button', {name: '予約'}).click();
    }

    async clickCancelButton() {
        await this.page.getByRole('link', {name: 'キャンセル'}).click();
    }

}