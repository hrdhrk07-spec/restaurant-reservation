import {Page} from '@playwright/test';

export class ReservationListPage {
    constructor(private page: Page) {
    }

    async clickCancelButton() {
        await this.page.getByRole('link', {name: '予約キャンセル'}).first().click();
    }

}