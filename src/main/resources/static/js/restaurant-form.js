/**
 * レストラン登録・編集画面の席詳細動的追加・削除処理
 */
document.addEventListener('DOMContentLoaded', function () {

    /**
     * 席詳細の追加ボタンクリック時の処理
     * 新しい席詳細の入力欄セットを追加する
     */
    document.querySelector('.add-seat-detail').addEventListener('click', function () {

        // 現在の件数を取得
        const count = document.querySelectorAll('.seat-detail-item').length;

        // 新しい入力欄セットのHTMLを生成
        const newItem = `
        <div class="seat-detail-item">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span>席詳細 ${count + 1}</span>
                <button type="button" class="btn btn-danger btn-sm remove-seat-detail">削除</button>
            </div>
            <div class="form-floating mb-3">
                <input class="form-control" id="personPerSeat_${count}"
                       name="seatDetails[${count}].personPerSeat"
                       type="number" min="1" max="100">
                <label for="personPerSeat_${count}">一席あたりの人数 <span class="text-danger">*</span></label>
            </div>
            <div class="form-floating mb-3">
                <input class="form-control" id="numberOfSeats_${count}"
                       name="seatDetails[${count}].numberOfSeats"
                       type="number" min="1">
                <label for="numberOfSeats_${count}">席セット数 <span class="text-danger">*</span></label>
            </div>
            <div class="form-floating mb-3">
                <input class="form-control" id="duration_${count}"
                       name="seatDetails[${count}].duration"
                       type="number" min="1">
                <label for="duration_${count}">所要時間（分） <span class="text-danger">*</span></label>
            </div>
        </div>
    `;

        // DOMに追加
        document.getElementById('seatDetailContainer').insertAdjacentHTML('beforeend', newItem);

    });

    /**
     * 席詳細の削除ボタンクリック時の処理
     * 対象の席詳細入力欄セットを削除し、インデックスを振り直す
     * 席詳細が1件の場合は削除不可
     */
    document.getElementById('seatDetailContainer').addEventListener('click', function (e) {
        if (e.target.classList.contains('remove-seat-detail')) {
            const items = document.querySelectorAll('.seat-detail-item');
            // 1件以上の場合のみ削除
            if (items.length > 1) {
                e.target.closest('.seat-detail-item').remove();
                // インデックスを振り直す
                reindexSeatDetails();
            }
        }
    });

    /**
     * 席詳細のインデックスを振り直す
     * 席詳細の削除後に呼び出す
     */
    function reindexSeatDetails() {
        document.querySelectorAll('.seat-detail-item').forEach((item, index) => {
            item.querySelectorAll('input').forEach(input => {
                input.name = input.name.replace(/\[\d+\]/, `[${index}]`);
                input.id = input.id.replace(/_\d+$/, `_${index}`);
            });
            item.querySelectorAll('label').forEach(label => {
                label.htmlFor = label.htmlFor.replace(/_\d+$/, `_${index}`);
            });
            item.querySelector('span').textContent = `席詳細 ${index + 1}`;
        });
    }

});

/**
 * 削除ボタンクリック時の確認ダイアログ表示
 */
const deleteButton = document.querySelector('input[value="レストラン削除"]');
if (deleteButton) {
    deleteButton.addEventListener('click', function (e) {
        if (!confirm('既存の予約も含めて削除します。本当に削除しますか？')) {
            e.preventDefault();
        }
    });
}