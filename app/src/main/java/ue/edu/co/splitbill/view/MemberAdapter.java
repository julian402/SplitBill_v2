package ue.edu.co.splitbill.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.Member;

// Llena la lista de integrantes. Tocar uno lo carga para editar; la caneca lo elimina
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    // Lo implementa la Activity: un metodo para editar y otro para eliminar
    public interface OnMemberListener {
        void onMemberClick(Member member);

        void onMemberDelete(Member member);
    }

    private final List<Member> memberList = new ArrayList<>();
    private final OnMemberListener listener;

    public MemberAdapter(OnMemberListener listener) {
        this.listener = listener;
    }

    // Cambia la lista y vuelve a pintar el RecyclerView
    public void updateData(List<Member> newMembers) {
        memberList.clear();
        if (newMembers != null) {
            memberList.addAll(newMembers);
        }
        notifyDataSetChanged();
    }

    // Infla el XML de una fila
    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    // Pone los datos de esa posicion en la fila
    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        holder.bind(memberList.get(position));
    }

    // Cuantas filas hay que mostrar
    @Override
    public int getItemCount() {
        return memberList.size();
    }

    // Guarda las vistas de una fila para no buscarlas cada vez
    class MemberViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMemberAvatar;
        private final TextView tvMemberName;
        private final TextView tvMemberPhone;
        private final ImageButton btnDeleteMember;

        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberAvatar = itemView.findViewById(R.id.tvMemberAvatar);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberPhone = itemView.findViewById(R.id.tvMemberPhone);
            btnDeleteMember = itemView.findViewById(R.id.btnDeleteMember);
        }

        // Llena la fila con los datos de un integrante
        void bind(Member member) {
            Format.avatar(itemView.getContext(), tvMemberAvatar, member.getName());
            tvMemberName.setText(member.getName());
            boolean hasPhone = member.getPhone() != null && !member.getPhone().isEmpty();
            tvMemberPhone.setText(hasPhone ? member.getPhone() : itemView.getContext().getString(R.string.tvNoPhone));
            itemView.setOnClickListener(view -> listener.onMemberClick(member));
            btnDeleteMember.setOnClickListener(view -> listener.onMemberDelete(member));
        }
    }
}
