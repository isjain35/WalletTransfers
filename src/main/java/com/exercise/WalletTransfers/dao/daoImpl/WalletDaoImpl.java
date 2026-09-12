package com.exercise.WalletTransfers.dao.daoImpl;

import com.exercise.WalletTransfers.dao.WalletDao;
import com.exercise.WalletTransfers.model.postgres.User;
import com.exercise.WalletTransfers.model.postgres.User_;
import com.exercise.WalletTransfers.model.postgres.Wallet;
import com.exercise.WalletTransfers.model.postgres.Wallet_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class WalletDaoImpl implements WalletDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public int debitWallet(Long id, Long amount) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaUpdate<Wallet> walletCriteriaUpdate = criteriaBuilder.createCriteriaUpdate(Wallet.class);
        Root<Wallet> root = walletCriteriaUpdate.from(Wallet.class);

        walletCriteriaUpdate.set(
                root.get(Wallet_.balance),
                criteriaBuilder.diff(root.get(Wallet_.balance), amount)
        ).where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(Wallet_.ID), id),
                        criteriaBuilder.greaterThanOrEqualTo(root.get(Wallet_.balance), amount)
                )
        );
        return entityManager.createQuery(walletCriteriaUpdate).executeUpdate();
    }

    @Override
    @Transactional
    public int creditWallet(Long id, Long amount) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaUpdate<Wallet> walletCriteriaUpdate = criteriaBuilder.createCriteriaUpdate(Wallet.class);
        Root<Wallet> root = walletCriteriaUpdate.from(Wallet.class);

        walletCriteriaUpdate.set(
                root.get(Wallet_.balance),
                criteriaBuilder.sum(root.get(Wallet_.balance), amount)
        ).where(
                criteriaBuilder.equal(root.get(Wallet_.ID), id)
        );
        return entityManager.createQuery(walletCriteriaUpdate).executeUpdate();
    }
}
