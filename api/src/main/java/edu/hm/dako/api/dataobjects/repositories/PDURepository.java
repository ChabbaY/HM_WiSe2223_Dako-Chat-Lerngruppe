package edu.hm.dako.api.dataobjects.repositories;

import edu.hm.dako.api.dataobjects.objects.PDU;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * interface to database (here in-memory)
 *
 * @author Linus Englert
 */
@Repository
@Component
@ComponentScan(basePackages = "edu.hm.dako.api")
public interface PDURepository extends JpaRepository<PDU, Long> {
}