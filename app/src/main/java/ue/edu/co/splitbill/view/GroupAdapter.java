package ue.edu.co.splitbill.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.Group;

// Llena la lista de grupos del inicio
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    // Lo implementa la Activity para saber que grupo se toco
    public interface OnGroupClickListener {
        void onGroupClick(Group group);
    }

    private final List<Group> groupList = new ArrayList<>();
    private final OnGroupClickListener listener;

    public GroupAdapter(OnGroupClickListener listener) {
        this.listener = listener;
    }

    // Cambia la lista y vuelve a pintar el RecyclerView
    public void updateData(List<Group> newGroups) {
        groupList.clear();
        if (newGroups != null) {
            groupList.addAll(newGroups);
        }
        notifyDataSetChanged();
    }

    // Infla el XML de una fila
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    // Pone los datos de esa posicion en la fila
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.bind(groupList.get(position));
    }

    // Cuantas filas hay que mostrar
    @Override
    public int getItemCount() {
        return groupList.size();
    }

    // Guarda las vistas de una fila para no buscarlas cada vez
    class GroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvGroupAvatar;
        private final TextView tvGroupName;
        private final TextView tvGroupMembers;
        private final TextView tvGroupTotal;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupAvatar = itemView.findViewById(R.id.tvGroupAvatar);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvGroupMembers = itemView.findViewById(R.id.tvGroupMembers);
            tvGroupTotal = itemView.findViewById(R.id.tvGroupTotal);
        }

        // Llena la fila con los datos de un grupo
        void bind(Group group) {
            Format.avatar(itemView.getContext(), tvGroupAvatar, group.getName());
            tvGroupName.setText(group.getName());
            long members = group.getMemberCount() == null ? 0 : group.getMemberCount();
            tvGroupMembers.setText(itemView.getContext().getResources()
                    .getQuantityString(R.plurals.membersCount, (int) members, (int) members));
            tvGroupTotal.setText(Format.money(group.getTotal() == null ? 0 : group.getTotal()));
            itemView.setOnClickListener(view -> listener.onGroupClick(group));
        }
    }
}
