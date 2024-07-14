NAME := rinha-de-backend-2023-q3
IMAGE_NAME := diegoalveslv/$(NAME)
IMAGE_TAG := latest

help :
	@echo ""
	@echo "*** $(NAME) Makefile help ***"
	@echo ""
	@echo "Targets list:"
	@grep -E '^[a-zA-Z_-]+ :.*?## .*$$' $(MAKEFILE_LIST) | sort -k 1,1 | awk 'BEGIN {FS = ":.*?## "}; {printf "\t\033[36m%-30s\033[0m %s\n", $$1, $$2}'
	@echo ""

clean-build-no-test : ## clean build no tests
	./gradlew clean build -x test

docker-build : clean-build-no-test	 ## Build the application
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) .

docker-build-run : docker-build ## Build and run app
	docker run -p 8080:8080 $(IMAGE_NAME):$(IMAGE_TAG)

update-docker-compose : docker-build	## down -v and up -d on docker compose
	docker compose down -v --remove-orphans
	docker compose up -d

gatling-run :	## run gatling
	./gradlew gatlingRun

update-docker-compose-run-gatling : update-docker-compose	##rebuild docker compose env and run gatling
	sleep 60
	make gatling-run

