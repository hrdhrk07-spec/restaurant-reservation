import {expect, Page, test} from '@playwright/test';
import {LoginPage} from '../pages/LoginPage';
import {HomePage} from '../pages/HomePage';
import {RestaurantListPage} from '../pages/RestaurantListPage';
import {RestaurantDetailPage} from '../pages/RestaurantDetailPage';
import {ReservationInputPage} from '../pages/ReservationInputPage';
import {ReservationConfirmPage} from '../pages/ReservationConfirmPage';
import {ReservationCompletePage} from '../pages/ReservationCompletePage';
import {ReservationListPage} from "../pages/ReservationListPage";
import {CancelConfirmPage} from "../pages/CancelConfirmPage";
import {CancelCompletePage} from "../pages/CancelCompletePage";

// 予約日時作成用のヘルパー関数
function getReservedAt(isPast: boolean, isHoliday: boolean, canReception: boolean, daysOffset: number) {

    // 現在日時を取得
    const date = new Date();

    // 過去日時かどうか
    if (isPast) {
        date.setDate(date.getDate() - 1);

    } else {
        date.setDate(date.getDate() + daysOffset);

        // 定休日かどうか
        if (isHoliday) {
            while (date.getDay() != Number(process.env.TEST_HOLIDAY_NUMBER1) && date.getDay() != Number(process.env.TEST_HOLIDAY_NUMBER2)) {
                date.setDate(date.getDate() + 1)
            }
        } else {
            while (date.getDay() == Number(process.env.TEST_HOLIDAY_NUMBER1) || date.getDay() == Number(process.env.TEST_HOLIDAY_NUMBER2)) {
                date.setDate(date.getDate() + 1)
            }
        }

        // 受付時間内かどうか
        if (canReception) {
            date.setHours(20, 0);
        } else {
            date.setHours(10, 0);
        }

    }

    // 文字列変換
    const year = date.getFullYear().toString().padStart(4, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hour = date.getHours().toString().padStart(2, '0');
    const minute = date.getMinutes().toString().padStart(2, '0');

    return year + '/' + month + '/' + day + ' ' + hour + ':' + minute;

}

// 異常系用の共通処理のヘルパー関数
async function goReservationInputPage(page: Page, homePage: HomePage, restaurantListPage: RestaurantListPage, restaurantDetailPage: RestaurantDetailPage) {

    // ホーム画面
    await homePage.goto();
    await homePage.fillSearchForm(process.env.TEST_LOCATION, process.env.TEST_CUISINE_TYPE, process.env.TEST_RESTAURANT_NAME);
    await homePage.clickSearchButton();

    // レストラン一覧画面
    await expect(page).toHaveTitle(/レストラン予約システム - レストラン一覧/);
    await restaurantListPage.clickRestaurant(process.env.TEST_RESTAURANT_NAME);

    // レストラン詳細画面
    await expect(page).toHaveTitle(/レストラン予約システム - レストラン詳細/);
    await restaurantDetailPage.clickReservationButton();

    // 予約入力画面
    await expect(page).toHaveTitle(/レストラン予約システム - 予約/);

}

test.describe('正常系', () => {

    let loginPage: LoginPage;
    let homePage: HomePage;
    let restaurantListPage: RestaurantListPage;
    let restaurantDetailPage: RestaurantDetailPage;
    let reservationInputPage: ReservationInputPage;
    let reservationConfirmPage: ReservationConfirmPage;
    let reservationCompletePage: ReservationCompletePage;
    let reservationListPage: ReservationListPage;
    let cancelConfirmPage: CancelConfirmPage;
    let cancelCompletePage: CancelCompletePage;


    test.beforeEach(async ({page}) => {

        // PageObjectの初期化
        loginPage = new LoginPage(page);
        homePage = new HomePage(page);
        restaurantListPage = new RestaurantListPage(page);
        restaurantDetailPage = new RestaurantDetailPage(page);
        reservationInputPage = new ReservationInputPage(page);
        reservationConfirmPage = new ReservationConfirmPage(page);
        reservationCompletePage = new ReservationCompletePage(page);
        reservationListPage = new ReservationListPage(page);
        cancelConfirmPage = new CancelConfirmPage(page);
        cancelCompletePage = new CancelCompletePage(page);

        // ログイン画面
        await loginPage.goto();
        await loginPage.fillLoginForm(process.env.TEST_USER_EMAIL ?? '', process.env.TEST_USER_PASSWORD ?? '');
        await loginPage.clickLoginButton();

        // ユーザ用ホーム画面
        await expect(page).toHaveTitle(/レストラン予約システム - ホーム/);

    });

    test('予約フロー全体', async ({page}) => {

        // ホーム画面
        await homePage.goto();
        await homePage.fillSearchForm(process.env.TEST_LOCATION, process.env.TEST_CUISINE_TYPE, process.env.TEST_RESTAURANT_NAME);
        await homePage.clickSearchButton();

        // レストラン一覧画面
        await expect(page).toHaveTitle(/レストラン予約システム - レストラン一覧/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.locator('p', {hasText: process.env.TEST_CUISINE_TYPE}).first()).toBeVisible();
        await restaurantListPage.clickRestaurant(process.env.TEST_RESTAURANT_NAME);

        // レストラン詳細画面
        await expect(page).toHaveTitle(/レストラン予約システム - レストラン詳細/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(process.env.TEST_LOCATION)).toBeVisible();
        await expect(page.locator('p', {hasText: process.env.TEST_CUISINE_TYPE}).first()).toBeVisible();
        await expect(page.getByText(process.env.TEST_HOLIDAY)).toBeVisible();
        await restaurantDetailPage.clickReservationButton();

        // 予約入力画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(process.env.TEST_RECEPTION_TIME)).toBeVisible();

        // 予約日時、人数入力
        const reservedAt = getReservedAt(false, false, true, 1);
        await reservationInputPage.fillReservationForm(reservedAt, process.env.TEST_NUMBER_OF_GUESTS);
        await reservationInputPage.clickSeatAvailabilityButton();

        // 席選択（ここでは２つ目の席詳細を選択）
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(reservedAt)).toBeVisible();
        await expect(page.locator('tr', {hasText: '予約人数'}).locator('span').first().getByText(process.env.TEST_NUMBER_OF_GUESTS)).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL1)).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL2)).toBeVisible();
        await reservationInputPage.clickSeatDetailRadioButton(process.env.TEST_SEAT_DETAIL2);
        await reservationInputPage.clickSeatSelectionButton();

        // 予約確認画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約確認/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(reservedAt)).toBeVisible();
        await expect(page.locator('tr', {hasText: '予約人数'}).locator('td').first().getByText(process.env.TEST_NUMBER_OF_GUESTS)).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL2)).toBeVisible();
        await reservationConfirmPage.clickReservationButton();

        // 予約完了画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約完了/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(reservedAt)).toBeVisible();
        await expect(page.getByText(process.env.TEST_NUMBER_OF_GUESTS + '人')).toBeVisible();
        await reservationCompletePage.clickReservationListButton();

        // 予約一覧画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約一覧/);
        const reservationRow = page.locator('.py-3.border-top', {hasText: reservedAt}).first();
        await expect(reservationRow.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(reservationRow.getByText(process.env.TEST_RESERVATION_STATUS_CONFIRMED)).toBeVisible();
        await reservationListPage.clickCancelButton();

        // キャンセル確認画面
        await expect(page).toHaveTitle(/レストラン予約システム - キャンセル確認/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(reservedAt)).toBeVisible();
        await expect(page.getByText(process.env.TEST_NUMBER_OF_GUESTS + '人')).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL2)).toBeVisible();
        await cancelConfirmPage.clickCancelButton();

        // キャンセル完了画面
        await expect(page).toHaveTitle(/レストラン予約システム - キャンセル完了/);
        await cancelCompletePage.clickReservationListButton();

        // 予約一覧画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約一覧/);
        await expect(reservationRow.getByText(process.env.TEST_RESERVATION_STATUS_CANCELLED)).toBeVisible();

    });

    test('予約確認でキャンセル後に再度予約フローを実施', async ({page}) => {

        // ホーム画面
        await homePage.goto();
        await homePage.fillSearchForm(process.env.TEST_LOCATION, process.env.TEST_CUISINE_TYPE, process.env.TEST_RESTAURANT_NAME);
        await homePage.clickSearchButton();

        // レストラン一覧画面
        await expect(page).toHaveTitle(/レストラン予約システム - レストラン一覧/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.locator('p', {hasText: process.env.TEST_CUISINE_TYPE}).first()).toBeVisible();
        await restaurantListPage.clickRestaurant(process.env.TEST_RESTAURANT_NAME);

        // レストラン詳細画面
        await expect(page).toHaveTitle(/レストラン予約システム - レストラン詳細/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(process.env.TEST_LOCATION)).toBeVisible();
        await expect(page.locator('p', {hasText: process.env.TEST_CUISINE_TYPE}).first()).toBeVisible();
        await expect(page.getByText(process.env.TEST_HOLIDAY)).toBeVisible();
        await restaurantDetailPage.clickReservationButton();

        // 予約1回目（予約確認時キャンセル）
        // 予約入力画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(process.env.TEST_RECEPTION_TIME)).toBeVisible();

        // 予約日時、人数入力
        const reservedAt = getReservedAt(false, false, true, 8);
        await reservationInputPage.fillReservationForm(reservedAt, process.env.TEST_NUMBER_OF_GUESTS);
        await reservationInputPage.clickSeatAvailabilityButton();

        // 席選択（ここでは２つ目の席詳細を選択）
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(reservedAt)).toBeVisible();
        await expect(page.locator('tr', {hasText: '予約人数'}).locator('span').first().getByText(process.env.TEST_NUMBER_OF_GUESTS)).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL1)).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL2)).toBeVisible();
        await reservationInputPage.clickSeatDetailRadioButton(process.env.TEST_SEAT_DETAIL2);
        await reservationInputPage.clickSeatSelectionButton();

        // 予約確認画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約確認/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(reservedAt)).toBeVisible();
        await expect(page.locator('tr', {hasText: '予約人数'}).locator('td').first().getByText(process.env.TEST_NUMBER_OF_GUESTS)).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL2)).toBeVisible();
        await reservationConfirmPage.clickCancelButton();

        // 予約2回目
        // 予約入力画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(process.env.TEST_RECEPTION_TIME)).toBeVisible();

        // 予約日時、人数入力
        await reservationInputPage.fillReservationForm(reservedAt, process.env.TEST_NUMBER_OF_GUESTS);
        await reservationInputPage.clickSeatAvailabilityButton();

        // 席選択（ここでは２つ目の席詳細を選択）
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(reservedAt)).toBeVisible();
        await expect(page.locator('tr', {hasText: '予約人数'}).locator('span').first().getByText(process.env.TEST_NUMBER_OF_GUESTS)).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL1)).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL2)).toBeVisible();
        await reservationInputPage.clickSeatDetailRadioButton(process.env.TEST_SEAT_DETAIL2);
        await reservationInputPage.clickSeatSelectionButton();

        // 予約確認画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約確認/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(reservedAt)).toBeVisible();
        await expect(page.locator('tr', {hasText: '予約人数'}).locator('td').first().getByText(process.env.TEST_NUMBER_OF_GUESTS)).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL2)).toBeVisible();
        await reservationConfirmPage.clickReservationButton();

        // 予約完了画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約完了/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(reservedAt)).toBeVisible();
        await expect(page.getByText(process.env.TEST_NUMBER_OF_GUESTS + '人')).toBeVisible();
        await reservationCompletePage.clickReservationListButton();

        // 予約一覧画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約一覧/);
        const reservationRow = page.locator('.py-3.border-top', {hasText: reservedAt}).first();
        await expect(reservationRow.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(reservationRow.getByText(process.env.TEST_RESERVATION_STATUS_CONFIRMED)).toBeVisible();
        await reservationListPage.clickCancelButton();

        // キャンセル確認画面
        await expect(page).toHaveTitle(/レストラン予約システム - キャンセル確認/);
        await expect(page.getByText(process.env.TEST_RESTAURANT_NAME)).toBeVisible();
        await expect(page.getByText(reservedAt)).toBeVisible();
        await expect(page.getByText(process.env.TEST_NUMBER_OF_GUESTS + '人')).toBeVisible();
        await expect(page.getByText(process.env.TEST_SEAT_DETAIL2)).toBeVisible();
        await cancelConfirmPage.clickCancelButton();

        // キャンセル完了画面
        await expect(page).toHaveTitle(/レストラン予約システム - キャンセル完了/);
        await cancelCompletePage.clickReservationListButton();

        // 予約一覧画面
        await expect(page).toHaveTitle(/レストラン予約システム - 予約一覧/);
        await expect(reservationRow.getByText(process.env.TEST_RESERVATION_STATUS_CANCELLED)).toBeVisible();

    });

});

test.describe('異常系', () => {

    let loginPage: LoginPage;
    let homePage: HomePage;
    let restaurantListPage: RestaurantListPage;
    let restaurantDetailPage: RestaurantDetailPage;
    let reservationInputPage: ReservationInputPage;

    test.beforeEach(async ({page}) => {

        // PageObjectの初期化
        loginPage = new LoginPage(page);
        homePage = new HomePage(page);
        restaurantListPage = new RestaurantListPage(page);
        restaurantDetailPage = new RestaurantDetailPage(page);
        reservationInputPage = new ReservationInputPage(page);

        // ログイン画面
        await loginPage.goto();
        await loginPage.fillLoginForm(process.env.TEST_USER_EMAIL ?? '', process.env.TEST_USER_PASSWORD ?? '');
        await loginPage.clickLoginButton();

        // ユーザ用ホーム画面
        await expect(page).toHaveTitle(/レストラン予約システム - ホーム/);

    });

    test('レストラン一覧で検索結果が無い', async ({page}) => {

        // ホーム画面
        await homePage.goto();
        await homePage.fillSearchForm('', '', 'あああああ');
        await homePage.clickSearchButton();

        // レストラン一覧画面
        await expect(page).toHaveTitle(/レストラン予約システム - レストラン一覧/);
        await expect(page.getByText('該当のレストランはありません。')).toBeVisible();

    });

    test('予約入力時過去日時エラー', async ({page}) => {

        // 予約入力画面へ進む
        await goReservationInputPage(page, homePage, restaurantListPage, restaurantDetailPage);

        // 予約日時、人数入力
        const reservedAt = getReservedAt(true, false, true, 0);
        await reservationInputPage.fillReservationForm(reservedAt, process.env.TEST_NUMBER_OF_GUESTS);
        await reservationInputPage.clickSeatAvailabilityButton();

        await expect(page.getByText('選択した日時は過去日時です。')).toBeVisible();

    });

    test('予約入力時受付時間外エラー', async ({page}) => {

        // 予約入力画面へ進む
        await goReservationInputPage(page, homePage, restaurantListPage, restaurantDetailPage);

        // 予約日時、人数入力
        const reservedAt = getReservedAt(false, false, false, 1);
        await reservationInputPage.fillReservationForm(reservedAt, process.env.TEST_NUMBER_OF_GUESTS);
        await reservationInputPage.clickSeatAvailabilityButton();

        await expect(page.getByText('選択した時刻は受付時間外です。')).toBeVisible();

    });

    test('席決定時対応する席詳細が無い', async ({page}) => {

        // 予約入力画面へ進む
        await goReservationInputPage(page, homePage, restaurantListPage, restaurantDetailPage);

        // 予約日時、人数入力
        const reservedAt = getReservedAt(false, false, true, 1);
        await reservationInputPage.fillReservationForm(reservedAt, '99');
        await reservationInputPage.clickSeatAvailabilityButton();

        await expect(page.getByText('条件に合う空席がありません。')).toBeVisible();

    });

});