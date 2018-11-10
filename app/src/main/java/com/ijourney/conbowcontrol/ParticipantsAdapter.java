package com.ijourney.conbowcontrol;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ijourney.conbowcontrol.databinding.RowParticipantsBinding;
import com.ijourney.p2plib.model.Host;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ParticipantVH> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;
    private List<Host> mParticipants;
    private Listener mListener;

    ParticipantsAdapter(Listener listener) {
        mParticipants = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public ParticipantVH onCreateViewHolder(ViewGroup parent, int viewType) {
        RowParticipantsBinding binding = RowParticipantsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ParticipantVH(viewType, binding);
    }

    @Override
    public void onBindViewHolder(final ParticipantVH holder, int position) {
        if (TYPE_HEADER == holder.mViewType) {
            holder.mBinding.nameTv.setText("=== Conbow Found: ===");
        } else {
            Host host = mParticipants.get(position - 1);
            holder.mBinding.nameTv.setText(host.getName());
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.sendChatRequest(mParticipants.get(holder.getAdapterPosition() - 1));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mParticipants.size() == 0 ? 0 : mParticipants.size() + 1;
    }

    public void setData(Set<Host> hosts) {
        mParticipants = new ArrayList<>(hosts);
        notifyDataSetChanged();
    }

    class ParticipantVH extends RecyclerView.ViewHolder {
        private final RowParticipantsBinding mBinding;
        private final int mViewType;

        ParticipantVH(int viewType, RowParticipantsBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mViewType = viewType;
        }
    }

    interface Listener {
        void sendChatRequest(Host host);
    }
}
