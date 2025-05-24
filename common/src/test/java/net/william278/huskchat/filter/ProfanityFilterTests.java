/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat.filter;

import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.user.TestOnlineUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assumptions;

public class ProfanityFilterTests {

    private ProfanityFilterer filterer;
    private boolean profanityFilterAvailable = false;

    @BeforeEach
    public void setUp() {
        try {
            filterer = new ProfanityFilterer(
                    new ProfanityFilterer.ProfanityFilterSettings(
                            "", ProfanityFilterer.ProfanityFilterMode.TOLERANCE, 0.8d
                    )
            );
            profanityFilterAvailable = true;
        } catch (RuntimeException | Error e) {
            // Check if it's an UnsatisfiedLinkError or caused by one
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof UnsatisfiedLinkError) {
                    // ProfanityChecker native library not available, skip tests
                    profanityFilterAvailable = false;
                    System.out.println("Skipping ProfanityFilter tests: Native library not available - " + cause.getMessage());
                    return;
                }
                cause = cause.getCause();
            }
            // If it's not related to UnsatisfiedLinkError, rethrow
            throw e;
        }
    }

    @Test
    public void givenSentenceContainingProfanity_testIsProfane() {
        Assumptions.assumeTrue(profanityFilterAvailable, "ProfanityFilter native library not available");
        final OnlineUser dummyPlayer = new TestOnlineUser();
        Assertions.assertFalse(filterer.isAllowed(dummyPlayer, "This is a fucking test sentence"));
        Assertions.assertFalse(filterer.isAllowed(dummyPlayer, "Shit"));
    }

    @Test
    public void givenNormalSentences_testIsNotProfane() {
        Assumptions.assumeTrue(profanityFilterAvailable, "ProfanityFilter native library not available");
        final OnlineUser dummyPlayer = new TestOnlineUser();
        Assertions.assertTrue(filterer.isAllowed(dummyPlayer, "AHOJ"));
        Assertions.assertTrue(filterer.isAllowed(dummyPlayer, "Hello"));
    }

    @Test
    public void givenObfuscatedProfanity_testIsProfane() {
        Assumptions.assumeTrue(profanityFilterAvailable, "ProfanityFilter native library not available");
        final OnlineUser dummyPlayer = new TestOnlineUser();
        Assertions.assertFalse(filterer.isAllowed(dummyPlayer, "You're a fuck1ng idiot"));
        Assertions.assertFalse(filterer.isAllowed(dummyPlayer, "Shut the h3ll up"));
    }

    @Test
    public void givenScunthorpe_testIsNotProfane() {
        Assumptions.assumeTrue(profanityFilterAvailable, "ProfanityFilter native library not available");
        final OnlineUser dummyPlayer = new TestOnlineUser();
        Assertions.assertTrue(filterer.isAllowed(dummyPlayer, "Scunthorpe"));
    }

    @Test
    public void givenLeetSpeak_testIsNotProfane() {
        Assumptions.assumeTrue(profanityFilterAvailable, "ProfanityFilter native library not available");
        final OnlineUser dummyPlayer = new TestOnlineUser();
        Assertions.assertTrue(filterer.isAllowed(dummyPlayer, "1337"));
    }
}
