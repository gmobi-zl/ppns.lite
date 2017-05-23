package com.poponews.lite.ui;

/**
 * Created by zl on 2016/10/21.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mocean.IAd;
import com.mocean.IAdItem;
import com.poponews.lite.activity.MNWebDetailActivity;
import com.poponews.lite.R;
import com.poponews.lite.model.MNCategory;
import com.poponews.lite.model.MNNews;
import com.poponews.lite.services.MonyNewsService;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private String categoryName;
    private ArrayList<Object> items;
    private final int NEWS = 0 , AD = 1;

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public RecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case NEWS:
                View view =
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_main, parent, false);
                viewHolder = new ViewHolderNews(view);
                break;
            case AD:
                View viewAd =
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_ad, parent, false);
                viewHolder = new ViewHolderAD(viewAd);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (items == null) return 0;
        Object posItem = items.get(position);
        if (posItem instanceof MNNews) {
            return NEWS;
        } else if (posItem instanceof IAdItem) {
            return AD;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case NEWS: {
                    final ViewHolderNews holderNews = (ViewHolderNews) holder;
                    final View view = holderNews.mView;
                    holderNews.index = position;
                    if (items != null){
                        MNNews news = (MNNews)items.get(position);
                        holderNews.title.setText(news.getTitle());
                        holderNews.source.setText(news.getSource());
                        holderNews.date.setText(news.getDateDay()+news.getDateHour());

                        if (news.getIcon() != null && holderNews.icon != null)
                            MonyNewsService.getInstance().getImageService().bind(news.getIcon(), holderNews.icon);
                    }

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MNNews news = (MNNews)items.get(holderNews.index);
                            MonyNewsService app = MonyNewsService.getInstance();
                            app.dataService.setCurrentDetailNews(news);

                            Intent intent = new Intent(mContext, MNWebDetailActivity.class);
                            mContext.startActivity(intent);
                        }
                    });
                }
                break;
            case AD:{
                    final ViewHolderAD holderAd = (ViewHolderAD) holder;

                    if (items != null){
                        final View view = holderAd.mView;
                        final IAdItem adItem = (IAdItem)items.get(position);
                        if (adItem.has(IAdItem.IMAGE)) {
                            adItem.bind(new String[]{IAdItem.IMAGE, IAdItem.TITLE},
                                    new View[]{holderAd.image, holderAd.fullTitle});
                        } else {
                            adItem.bind(new String[]{IAdItem.ICON, IAdItem.TITLE, IAdItem.SUBTITLE},
                                    new View[]{holderAd.icon, holderAd.title, holderAd.body});
                        }

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adItem.execute("go", null);
                            }
                        });
                    }
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        MonyNewsService mnSvr = MonyNewsService.getInstance();
        MNCategory category = mnSvr.dataService.findCurrentChannelCategoryByName(categoryName);
        int perADIndex = 3;
        if (category != null){
            ArrayList<MNNews> newsList = category.getNews();
            ArrayList<IAdItem> adList = category.getAds();
            if (newsList == null)
                return 0;

            if (items == null) {
                items = new ArrayList<Object>();
                int maxAdNum = newsList.size() / perADIndex;
                if (adList == null && mnSvr.dataService.hasActiviteAd()){
                    adList = new ArrayList<IAdItem>();
                    for (int i = 0; i < maxAdNum; i++){
                        IAdItem ad = mnSvr.dataService.getAcitiveAd();
                        if (ad == null)
                            break;
                        adList.add(ad);
                    }
                    if (adList.size() > 0)
                        category.setAds(adList);
                }

                for (int j = 0; j < newsList.size(); j++){
                    MNNews mNews = newsList.get(j);
                    items.add(mNews);
                }

                if (adList != null){
                    for (int m = 1; m <= adList.size(); m++){
                        IAdItem ad = adList.get(m-1);
                        int insertIndex = (perADIndex * m) + (m - 1);
                        if (insertIndex < items.size())
                            items.add(insertIndex, ad);
                        else
                            items.add(ad);
                    }
                }
                if (items != null) {
                    return items.size();
                }
            } else {
                int itemCount = items.size();
                int maxAdNum = newsList.size() / perADIndex;

                if (adList == null && mnSvr.dataService.hasActiviteAd()){
                    adList = new ArrayList<IAdItem>();
                    for (int i = 0; i < maxAdNum; i++){
                        IAdItem ad = mnSvr.dataService.getAcitiveAd();
                        if (ad == null)
                            break;
                        adList.add(ad);
                    }
                    if (adList.size() > 0)
                        category.setAds(adList);
                }

                int adCount = adList == null ? 0 : adList.size();
                int newsCount = newsList.size();
                if (itemCount < adCount + newsCount){
                    items.clear();
                    for (int j = 0; j < newsList.size(); j++){
                        MNNews mNews = newsList.get(j);
                        items.add(mNews);
                    }

                    if (adList != null){
                        for (int m = 1; m <= adList.size(); m++){
                            IAdItem ad = adList.get(m-1);
                            int insertIndex = (perADIndex * m) + (m - 1);
                            if (insertIndex < items.size())
                                items.add(insertIndex, ad);
                            else
                                items.add(ad);
                        }
                    }
                }
                if (items != null) {
                    return items.size();
                }
            }
        }

        return 0;
    }

    public static class ViewHolderNews extends RecyclerView.ViewHolder {
        public final View mView;

        TextView title;
        ImageView icon;
        TextView source;
        TextView date;
        int index;

        public ViewHolderNews(View view) {
            super(view);
            mView = view;

            title = (TextView)view.findViewById(R.id.tvTitle);
            icon = (ImageView)view.findViewById(R.id.ivIcon);
            source = (TextView)view.findViewById(R.id.tvRssSource);
            date = (TextView)view.findViewById(R.id.tvDate);
        }
    }

    public static class ViewHolderAD extends RecyclerView.ViewHolder {
        public final View mView;

        ImageView icon;
        TextView title;
        TextView body;
        ImageView image;
        TextView fullTitle;

        public ViewHolderAD(View view) {
            super(view);
            mView = view;

            title = (TextView)view.findViewById(R.id.tvTitle);
            icon = (ImageView)view.findViewById(R.id.ivIcon);
            body = (TextView)view.findViewById(R.id.tvBody);
            image = (ImageView)view.findViewById(R.id.ivImage);
            fullTitle = (TextView)view.findViewById(R.id.tvFullTitle);
        }
    }
}