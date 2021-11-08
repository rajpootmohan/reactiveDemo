//package com.example.reactiveDemo.configuration;
//
//import com.example.reactiveDemo.model.Quote;
//import com.example.reactiveDemo.repository.QuoteMongoReactiveRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//
//import java.io.*;
//import java.util.function.Supplier;
//
//@Component
//public class QuijoteDataLoader implements ApplicationRunner {
//
//    private static final Logger log = LoggerFactory.getLogger(QuijoteDataLoader.class);
//
//    private final QuoteMongoReactiveRepository quoteMongoReactiveRepository;
//
//    QuijoteDataLoader(final QuoteMongoReactiveRepository quoteMongoReactiveRepository) {
//        this.quoteMongoReactiveRepository = quoteMongoReactiveRepository;
//    }
//
//    @Override
//    public void run(final ApplicationArguments args) {
//        if (quoteMongoReactiveRepository.count().block() == 0L) {
//            var idSupplier = getIdSequenceSupplier();
//            File file = new File("pg2000.txt");
//            BufferedReader reader = null;
//            try {
//                reader = new BufferedReader(new FileReader(file));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            Flux.fromStream(
//                    reader.lines()
//                            .filter(l -> !l.trim().isEmpty())
//                            .map(l -> quoteMongoReactiveRepository.save(
//                                    new Quote(idSupplier.get(),
//                                            "El Quijote", l))
//                            )
//            ).subscribe(m -> log.info("New quote loaded: {}", m.block()));
//            log.info("Repository contains now {} entries.",
//                    quoteMongoReactiveRepository.count().block());
//        }
//    }
//
//    private Supplier<String> getIdSequenceSupplier() {
//        return new Supplier<>() {
//            Long l = 0L;
//
//            @Override
//            public String get() {
//                // adds padding zeroes
//                return String.format("%05d", l++);
//            }
//        };
//    }
//}
