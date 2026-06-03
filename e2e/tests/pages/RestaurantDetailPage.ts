import {Page} from '@playwright/test';

export class RestaurantDetailPage {
    constructor(private page: Page) {
    }

    async clickReservationButton() {
        await this.page.getByRole('button', {name: '予約'}).click();
    }

}