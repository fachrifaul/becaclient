package id.web.go_cak.sewa.view.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.web.go_cak.sewa.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Jasa> mItems;
    private Context context;
    private OnItemClickListener listener;

    public MainAdapter(Context context) {
        super();
        mItems = new ArrayList<>();
        Jasa species = new Jasa();
        species.setName("Antar Jemput");
        species.setThumbnail(R.drawable.bentor);
        mItems.add(species);

        species = new Jasa();
        species.setName("Gocak Services");
        species.setThumbnail(R.drawable.paket);
        mItems.add(species);
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Jasa endangeredItem = mItems.get(position);
        holder.tvspecies.setText(endangeredItem.getName());
        holder.imgThumbnail.setImageResource(endangeredItem.getThumbnail());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(endangeredItem, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgThumbnail;
        public TextView tvspecies;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvspecies = (TextView) itemView.findViewById(R.id.tv_species);
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Jasa item, int position);
    }
}