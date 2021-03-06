// Teoria Współbieżnośi, implementacja problemu 5 filozofów w node.js
// Opis problemu: http://en.wikipedia.org/wiki/Dining_philosophers_problem
//   https://pl.wikipedia.org/wiki/Problem_ucztuj%C4%85cych_filozof%C3%B3w
// 1. Dokończ implementację funkcji podnoszenia widelca (Fork.acquire).
// 2. Zaimplementuj "naiwny" algorytm (każdy filozof podnosi najpierw lewy, potem
//    prawy widelec, itd.).
// 3. Zaimplementuj rozwiązanie asymetryczne: filozofowie z nieparzystym numerem
//    najpierw podnoszą widelec lewy, z parzystym -- prawy.
// 4. Zaimplementuj rozwiązanie z kelnerem (według polskiej wersji strony)
// 5. Zaimplementuj rozwiążanie z jednoczesnym podnoszeniem widelców:
//    filozof albo podnosi jednocześnie oba widelce, albo żadnego.
// 6. Uruchom eksperymenty dla różnej liczby filozofów i dla każdego wariantu
//    implementacji zmierz średni czas oczekiwania każdego filozofa na dostęp
//    do widelców. Wyniki przedstaw na wykresach.

const sleep = (ms) => new Promise(res => setTimeout(res, ms));
var waitTimeSum = 0;
var ended = 0;

var Fork = function() {
    this.state = 0;
    return this;
}

Fork.prototype.acquire = async function(callback) {
    // zaimplementuj funkcję acquire, tak by korzystala z algorytmu BEB
    // (http://pl.wikipedia.org/wiki/Binary_Exponential_Backoff), tzn:
    // 1. przed pierwszą próbą podniesienia widelca Filozof odczekuje 1ms
    // 2. gdy próba jest nieudana, zwiększa czas oczekiwania dwukrotnie
    //    i ponawia próbę, itd.
    waitTime = 1
    await sleep(waitTime)
    while(this.state != 0){
        waitTime *= 2;
        await sleep(waitTime);
    }
    this.state = 1;
    callback();
}

Fork.prototype.release = function() {
    this.state = 0;
}

var Philosopher = function(id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id+1) % forks.length;
    return this;
}

Philosopher.prototype.startNaive = function(count) {
    // zaimplementuj rozwiązanie naiwne
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców
    const forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id;

    const printWrapper = function(text){
        console.log(`Philosopher (${id}): ${text}.`);
    }

    const think = async function(){
        printWrapper('Thinking');
    }

    const eat = async function(){
        printWrapper('Eating');
    }

    const task = async function(step){
        if (step >= count)
            return;

        var leftFork = forks[f1];
        var rightFork = forks[f2];

        await think();
        await leftFork.acquire(() => {printWrapper('Acquire left fork')});
        await rightFork.acquire(() => {printWrapper('Acquire right fork')});

        await eat();
        await leftFork.release(() => {printWrapper('Release left fork')});
        await rightFork.release(() => {printWrapper('Release right fork')});

        task(step + 1);
    }

    task(0);
}

Philosopher.prototype.startAsym = async function(count) {
    // zaimplementuj rozwiązanie asymetryczne
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców

    const forks = this.forks,
        id = this.id,
        f1 = this.f1,
        f2 = this.f2;

    const printWrapper = function(text){
        console.log(`Philosopher (${id}): ${text}.`);
    }

    const think = async function(){
        printWrapper('Thinking');
    }

    const eat = async function(){
        printWrapper('Eating');
    }
    const task = async function(step){
        if (step >= count)
            return;

        var leftFork = forks[f1];
        var rightFork = forks[f2];

        await think();
        var start = process.hrtime();
        if(id % 2 != 0){
            await leftFork.acquire(() => {printWrapper('Acquire left fork')});
            await rightFork.acquire(() => {printWrapper('Acquire right fork')});
        }else{
            await rightFork.acquire(() => {printWrapper('Acquire right fork')});
            await leftFork.acquire(() => {printWrapper('Acquire left fork')});
        }
        waitTimeSum+=process.hrtime(start)[1];

        await eat();
        await leftFork.release(() => {printWrapper('Release left fork')});
        await rightFork.release(() => {printWrapper('Release right fork')});

        await task(step + 1);
    }

    await task(0);
    ended++;
}

var Conductor = function(){
    this.state = 0;
    return this;
}

Conductor.prototype.acquire = async function(callback){
    waitTime = 1;
    await sleep(waitTime);
    while(this.state != 0){
        waitTime *= 2;
        await sleep(waitTime);
    }
    this.state = 1;
    callback();
}

Conductor.prototype.release = function(){
    this.state = 0;
}

Philosopher.prototype.startConductor = async function(count, conductor) {
    // zaimplementuj rozwiązanie z kelnerem
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców

    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id;

    const printWrapper = function(text){
        console.log(`Philosopher (${id}): ${text}.`);
    }

    const think = async function(){
        printWrapper('Thinking');
    }

    const eat = async function(){
        printWrapper('Eating');
    }
    const task = async function(step){
        if (step >= count)
            return;
        await printWrapper(`Conductor before state ${conductor.state}`);
        await conductor.acquire(() => {printWrapper('Acquire conductor')});
        await printWrapper(`Conductor after state ${conductor.state}`);
        var leftFork = forks[f1];
        var rightFork = forks[f2];

        await think();
        var start = process.hrtime();
        await leftFork.acquire(() => {printWrapper('Acquire left fork')});
        await rightFork.acquire(() => {printWrapper('Acquire right fork')});
        waitTimeSum+=process.hrtime(start)[1];

        await eat();
        await leftFork.release(() => {printWrapper('Release left fork')});
        await rightFork.release(() => {printWrapper('Release right fork')});

        await conductor.release();

        await task(step + 1);
    }

    await task(0);
    ended++;
}

// TODO: wersja z jednoczesnym podnoszeniem widelców
// Algorytm BEB powinien obejmować podnoszenie obu widelców,
// a nie każdego z osobna

var acquireBothForks = async function(left, right, callback){
    waitTime = 1;
    await sleep(waitTime);
    while(left.state != 0 || right.state != 0){
        waitTime *= 2;
        await sleep(waitTime);
    }
    left.state = 1;
    right.state = 1;
    callback();
};

Philosopher.prototype.startBothForks = async function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id;

    const printWrapper = function(text){
        console.log(`Philosopher (${id}): ${text}.`);
    }

    const think = async function(){
        printWrapper('Thinking');
    }

    const eat = async function(){
        printWrapper('Eating');
    }
    const task = async function(step){
        if (step >= count)
            return;
        var leftFork = forks[f1];
        var rightFork = forks[f2];

        await think();
        var start = process.hrtime();
        await acquireBothForks(leftFork, rightFork, () => {printWrapper('Acquire both forks')});
        waitTimeSum+=process.hrtime(start)[1];

        await eat();
        await leftFork.release(() => {printWrapper('Release left fork')});
        await rightFork.release(() => {printWrapper('Release right fork')});

        await task(step + 1);
    }

    await task(0);
    ended++;
};

const nPhilosophers = 10,
    count = 10,
    version = 'both_forks';

function main(version){
    const versionRunner = Object.freeze({
        'asym': 'startAsym',
        'naive': 'startNaive',
        'conductor': 'startConductor',
        'both_forks': 'startBothForks',
    });

    var conductor = new Conductor();

    const run = async (philosophers, version, count) => {
        if(version === 'conductor'){
            return philosophers[i][versionRunner['conductor']](count, conductor);
        }
        return philosophers[i][versionRunner[version]](count);
    };

    console.log(`Version: ${version}`);

    var forks = [];
    var philosophers = []
    for (var i = 0; i < nPhilosophers; i++)
        forks.push(new Fork());

    for (var i = 0; i < nPhilosophers; i++)
        philosophers.push(new Philosopher(i, forks));

    var promises = []
    for (var i = 0; i < nPhilosophers; i++)
        promises.push(run(philosophers, version, count));

    Promise.all(promises).then(() => {
        if(ended = nPhilosophers)
        console.log(`${waitTimeSum / nPhilosophers / 1000000}`);
    });
}

main(version);
