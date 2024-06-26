package com.example.plug_n_play;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ApplicationRunner implements CommandLineRunner {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private TokenTextSplitter tokenTextSplitter;

    @Override
    public void run(String... args) throws Exception {
        customerRepository.deleteAll();

        // save a couple of customers
        customerRepository.save(new Customer("Alice", "Smith", "She lives in Targovishte, Bulgaria. She is kind and easy-going person. She loves cats."));
        customerRepository.save(new Customer("Bob", "Smith", "He lives in Targovishte, Romania. He is kind and generous person. He loves dogs but does not mind cats."));

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        List<Document> documents = new ArrayList<>();
        for (Customer customer : customerRepository.findAll()) {
            System.out.println(customer);
            Document document = new Document(
                    customer.firstName,
                    Map.of(
                            "firstName", customer.firstName,
                            "id", customer.id,
                            "lastName", customer.lastName,
                            "description", customer.description
                    )
            );
            documents.add(document);
        }
        vectorStore.add(tokenTextSplitter.apply(documents));
        System.out.println(vectorStore.similaritySearch(SearchRequest.query("Alice").withTopK(5)));
    }
}
