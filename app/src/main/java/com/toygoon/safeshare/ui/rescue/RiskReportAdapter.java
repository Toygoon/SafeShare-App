package com.toygoon.safeshare.ui.rescue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.toygoon.safeshare.R;
import com.toygoon.safeshare.data.RiskReportDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RiskReportAdapter extends ArrayAdapter<RiskReportDTO> {
    private LayoutInflater inflater;
    private int resource;
    private Context context;

    public RiskReportAdapter(@NonNull Context context, int resource, @NonNull List<RiskReportDTO> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = inflater.inflate(resource, null);
            viewHolder = new ViewHolder();
            viewHolder.textTitle = view.findViewById(R.id.textTitle);
            viewHolder.textSummary = view.findViewById(R.id.textSummary);
            viewHolder.textReportedAt = view.findViewById(R.id.textReportedAt);
            viewHolder.textIsSolved = view.findViewById(R.id.textIsSolved);
            viewHolder.textLatLng = view.findViewById(R.id.textLatLng);
            viewHolder.textRiskFactor = view.findViewById(R.id.textRiskFactor);
            viewHolder.textUser = view.findViewById(R.id.textUser);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        RiskReportDTO item = getItem(position);
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");

        SimpleDateFormat resultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        resultDateFormat.setTimeZone(timeZone);
        String reportedAtString = resultDateFormat.format(item.reportedAt),
                isSolvedString = context.getString(R.string.risk_label_is_solved) + " : " +
                        (item.isSolved ? context.getString(R.string.risk_label_solved) : context.getString(R.string.risk_label_unsolved)),
                latLngString = context.getString(R.string.location) + " : (" + item.latlng.lat + ", " + item.latlng.lng + ")";

        StringBuffer sb = new StringBuffer();
        sb.append(context.getString(R.string.risk_label_type));
        sb.append(" : ");
        sb.append(item.riskFactor.name);
        sb.append("\n");
        sb.append(context.getString(R.string.risk_label_level));
        sb.append(" : ");
        sb.append(item.riskFactor.riskLevel);
        sb.append("\n");
        sb.append(context.getString(R.string.risk_label_impact));
        sb.append(" : ");
        sb.append(item.riskFactor.riskImpact);

        String riskFactorString = sb.toString(),
                userString = context.getString(R.string.risk_label_user) + " : " + item.user;

        viewHolder.textTitle.setText(context.getString(R.string.risk_label_title) + " : " + item.title);
        viewHolder.textSummary.setText(context.getString(R.string.risk_label_content) + " : " + item.summary);

        viewHolder.textReportedAt.setText(context.getString(R.string.risk_label_time) + " : " + reportedAtString);
        viewHolder.textIsSolved.setText(isSolvedString);

        viewHolder.textLatLng.setText(latLngString);
        viewHolder.textRiskFactor.setText(riskFactorString);
        viewHolder.textUser.setText(userString);

        CardView cardView = (CardView) view.findViewById(R.id.card_view);

        if (item.isSolved)
            cardView.setCardBackgroundColor(context.getColor(R.color.green_200));
        else
            cardView.setCardBackgroundColor(Color.parseColor("#f48fb1"));

        return view;
    }

    private static class ViewHolder {
        TextView textTitle;
        TextView textSummary;
        TextView textReportedAt;
        TextView textIsSolved;
        TextView textLatLng;
        TextView textRiskFactor;
        TextView textUser;
    }
}
