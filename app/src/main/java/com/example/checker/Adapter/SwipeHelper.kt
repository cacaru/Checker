package com.example.checker.Adapter

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeHelper : ItemTouchHelper.Callback() {

    private var cx : Float = 0f;
    private var fixed_x : Float = 0f;
    private var max_x : Float = 0f;

    // 스와이프 기능을 이용할 것인지 확인하는 함수
    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }
    // 길게 눌러서 발생하는 이벤트 
    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    // 이동 방향
    // 움직이는 모든 함수보다 먼저 실행
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // 스와이프 최댓값 저장 - 최대 스와이프 거리를 25%로 제한
        val view = (viewHolder as CheckListViewAdapter.ChecklistViewHolder).list_view;
        max_x = -(view.width.toFloat() / 100 * 25);
        // 고정될 부분은 20%로 제한
        fixed_x = -(view.width.toFloat() / 10 * 2);
        // drag 안함
        // swipe는 왼쪽으로만
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT);
    }

    // drag
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false;
    }

    // 끝까지 다 스와이프 되어 아이템이 사라졌을 때 호출
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    // 한 아이템만 움직이게 할 떄 사용함
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val fixed_val = (viewHolder as CheckListViewAdapter.ChecklistViewHolder).get_fixed();
            val view = (viewHolder as CheckListViewAdapter.ChecklistViewHolder).list_view;

            var x = when {
                fixed_val -> if (isCurrentlyActive)  fixed_x + dX else fixed_x
                dX < max_x -> max_x
                else ->  dX
            }
            if ( x > 0 ) x = 0f;
            cx = dX;
            getDefaultUIUtil().onDraw(c, recyclerView, view, x, dY, actionState, isCurrentlyActive);
        }
    }

    // 없앨 속도 지정하는 함수
    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        //return super.getSwipeEscapeVelocity(defaultValue)
        return defaultValue * 20
    }

    // 어느정도 스와이프 하면 아이템을 제거할 지 정하는 함수
    // 터치 유지하다가 떼면 호출됨
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        //Log.d("checker_swipe_test", cx.toString() + " " + fixed_x.toString());
        (viewHolder as CheckListViewAdapter.ChecklistViewHolder).change_fixed(cx < max_x);

        //return super.getSwipeThreshold(viewHolder)
        return 2f
    }
}