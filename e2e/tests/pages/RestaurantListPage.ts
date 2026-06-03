import {Page} from '@playwright/test';

export class RestaurantListPage {
    constructor(private page: Page) {
    }

    async clickRestaurant(restaurantName: string) {
        await this.page.getByRole('link', {name: restaurantName}).click();
    }

}