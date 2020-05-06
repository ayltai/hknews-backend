package com.github.ayltai.hknews.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

@Component
public final class ParserFactory {
    //region Constants

    public static final String SOURCE_APPLE_DAILY       = "蘋果日報";
    public static final String SOURCE_HEADLINE          = "頭條日報";
    public static final String SOURCE_HEADLINE_REALTIME = "頭條即時";
    public static final String SOURCE_HKEJ              = "信報";
    public static final String SOURCE_HKET              = "經濟日報";
    public static final String SOURCE_MING_PAO          = "明報";
    public static final String SOURCE_ORIENTAL_DAILY    = "東方日報";
    public static final String SOURCE_RTHK              = "香港電台";
    public static final String SOURCE_SCMP              = "南華早報";
    public static final String SOURCE_SING_PAO          = "成報";
    public static final String SOURCE_SING_TAO          = "星島日報";
    public static final String SOURCE_SING_TAO_REALTIME = "星島即時";
    public static final String SOURCE_SKYPOST           = "晴報";
    public static final String SOURCE_THE_STANDARD      = "英文虎報";
    public static final String SOURCE_WEN_WEI_PO        = "文匯報";

    //endregion

    //region Variables

    private static ParserFactory instance;

    private final ApiServiceFactory apiServiceFactory;
    private final SourceRepository  sourceRepository;

    //endregion

    public static ParserFactory getInstance(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository) {
        if (ParserFactory.instance == null) ParserFactory.instance = new ParserFactory(apiServiceFactory, sourceRepository);

        return ParserFactory.instance;
    }

    @Autowired
    private ParserFactory(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository) {
        this.apiServiceFactory = apiServiceFactory;
        this.sourceRepository  = sourceRepository;
    }

    @SuppressWarnings("CyclomaticComplexity")
    @NonNull
    public Parser create(@NonNull @lombok.NonNull final String sourceName) {
        switch (sourceName) {
            case ParserFactory.SOURCE_APPLE_DAILY:
                return new AppleDailyParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_HEADLINE:
                return new HeadlineParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_HEADLINE_REALTIME:
                return new HeadlineRealtimeParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_HKEJ:
                return new HkejParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_HKET:
                return new HketParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_MING_PAO:
                return new MingPaoParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_ORIENTAL_DAILY:
                return new OrientalDailyParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_RTHK:
                return new RthkParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_SCMP:
                return new ScmpParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_SING_PAO:
                return new SingPaoParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_SING_TAO:
                return new SingTaoParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_SING_TAO_REALTIME:
                return new SingTaoRealtimeParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_SKYPOST:
                return new SkyPostParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_THE_STANDARD:
                return new TheStandardParser(this.apiServiceFactory, this.sourceRepository);

            case ParserFactory.SOURCE_WEN_WEI_PO:
                return new WenWeiPoParser(this.apiServiceFactory, this.sourceRepository);

            default:
                throw new IllegalArgumentException("Unrecognized source name " + sourceName);
        }
    }
}
