package com.am.toolbarsearch.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.am.toolbarsearch.R;

/**
 * User: LJM
 * Date&Time: 2016-10-21 & 14:07
 * Describe: Describe Text
 */
public class IndexActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        findViewById(R.id.mTvSimpleSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IndexActivity.this,SimpleSearchActivity.class));
            }
        });
        findViewById(R.id.mTvToolBarSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IndexActivity.this,ToolBarSearchActivity.class));
            }
        });

        findViewById(R.id.mTvToolBarSearchDiy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IndexActivity.this,ToolBarSearchDiyActivity.class));
            }
        });
        findViewById(R.id.mTvToolBarSearchJump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IndexActivity.this,JustListActivity.class));
            }
        });

        findViewById(R.id.mTvLvFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IndexActivity.this,LvFilterSearchActivity.class));
            }
        });
        findViewById(R.id.mTvRvFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IndexActivity.this,RecyclerViewFliterSearchActivity.class));
            }
        });



    }
}
